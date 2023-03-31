package cn.zhanghui.demo.daily.netty.httpfile;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.ChunkedFile;
import io.netty.util.CharsetUtil;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Pattern;
/*
 * 服务端业务处理类
 */
public class ServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
	
    private final String url;
    
    //非法的url标识
    private static final Pattern INSECURE_URL = Pattern.compile(".*[<>&\"].*");
    
    public ServerHandler(String url) {
		this.url = url;
	}
	
	@Override
	protected void messageReceived(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
		//对请求的编码结果进行判断
		if(! req.decoderResult().isSuccess()) {
			sendError(ctx, HttpResponseStatus.BAD_REQUEST);
			return;
		}
		
		//对请求方式进行判断
		if(req.method() != HttpMethod.GET) {
			sendError(ctx, HttpResponseStatus.METHOD_NOT_ALLOWED);
			return;
		}
		final String uri = req.uri();
		final String path = sanitizeUri(uri);
		if(path == null) {
			sendError(ctx, HttpResponseStatus.FORBIDDEN);
			return;
		}
		File file = new File(path);
		if(file.isHidden() || !file.exists()) {
			sendError(ctx, HttpResponseStatus.NOT_FOUND);
			return;
		}
		if(file.isDirectory()) {
			if(uri.endsWith("/")) {
			//如果以正常“/”结束说明访问的是一个文件目录，则进行展示文件列表
				sendListing(ctx,file);
			}else {
				sendRedirect(ctx,uri + "/")	;
			}
		    return;
		}
		if(!file.isFile()) {
			sendError(ctx, HttpResponseStatus.FORBIDDEN);
			return;
		}
		RandomAccessFile randomAccessFile = null;
		try {
			randomAccessFile = new RandomAccessFile(file, "r");
		} catch (Exception e) {
			sendError(ctx, HttpResponseStatus.NOT_FOUND);
			return;
		}
		long fileLength = randomAccessFile.length();
		HttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
		HttpHeaderUtil.setContentLength(response, fileLength);
		setContentTypeHeader(response, file);
		if(HttpHeaderUtil.isKeepAlive(req)) {
			response.headers().set("Connection",HttpHeaderValues.KEEP_ALIVE);
		}
		ctx.write(response);
		//构造发送文件线程，将文件写到chunk缓冲区
		ChannelFuture sendFileFuture;
		sendFileFuture = ctx.write(new ChunkedFile(randomAccessFile,0,fileLength,8192), ctx.newProgressivePromise());
		//添加传输监听
		sendFileFuture.addListener(new ChannelProgressiveFutureListener() {
			@Override
			public void operationComplete(ChannelProgressiveFuture arg0) throws Exception {
				System.out.println("Transfer complete !");
			}
			@Override
			public void operationProgressed(ChannelProgressiveFuture future, long progress, long total) throws Exception {
				if(total < 0) {
					System.err.println("Transfer progress: " + progress);
				} else {
					System.err.println("Transfer progress: " + progress + "/" + total);
				}
				
			}
		});
		//如果使用Chunked编码，最后则需要发送一个编码结束的空消息体，进行标记，表示所有消息体已经成功发送，
		ChannelFuture lastContentFuture  = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
		if(!HttpHeaderUtil.isKeepAlive(req)) {
			lastContentFuture.addListener(ChannelFutureListener.CLOSE);
		}
	}
	
	//捕获到异常时
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		if(ctx.channel().isActive()) {
			sendError(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR);
			ctx.close();
		}
	}
    
	public String sanitizeUri(String uri) {
		try {
			uri = URLDecoder.decode(uri, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			try {
				uri = URLDecoder.decode(uri, "ISO-8859-1");
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		//对uri进行细粒度判断
		if(!uri.startsWith(url)) {
			return null;
		}
		if(!uri.startsWith("/")) {
			return null;
		}
		uri = uri.replace("/", File.separator);
		if(uri.contains(File.separator+".") || uri.contains("."+File.separator)||INSECURE_URL.matcher(uri).matches()) {
			return null;
		}
		return System.getProperty("user.dir")+uri;
	}
	//
    private static final Pattern ALLOWED_FILE_NAME = Pattern.compile("[A-Za-z0-9][-_A-Za-z0-9\\.]*");
    
    public static void sendListing(ChannelHandlerContext ctx, File dir) {
    	FullHttpResponse response =new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
    	response.headers().set("Content-Type", "text/html; charset=UTF-8");
    	StringBuilder sb = new StringBuilder();
    	String dirPath = dir.getPath();
    	sb.append("<!DOCTYPE html>\r\n");
    	sb.append("<html><head><title>");
    	sb.append(dirPath);
    	sb.append("目录：");
    	sb.append("</title></head><body>\r\n");
    	sb.append("<h3>");
    	sb.append(dirPath).append(" 目录:");
    	sb.append("</h3>\r\n");
    	sb.append("<ul>");
    	sb.append("<li>链接:<a href=\"../\">..</a></li>\r\n");
    	
    	//遍历文件 添加超链接
    	for(File f : dir.listFiles()) {
    		if(f.isHidden() || !f.canRead()) {
    			continue;
    		}
    		String name = f.getName();
    		if(!ALLOWED_FILE_NAME.matcher(name).matches()) {
    			continue;
    		}
    		sb.append("<li>链接:<a href=\"");
    		sb.append(name);
    		sb.append("\">");
    		sb.append(name);
    		sb.append("</a></li>\r\n");
    	}
    	sb.append("</ul></body></html>\r\n");
    	ByteBuf buffer = Unpooled.copiedBuffer(sb, CharsetUtil.UTF_8);
    	response.content().writeBytes(buffer);
    	buffer.release();
    	ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
	//错误信息
	public static void sendError(ChannelHandlerContext ctx, HttpResponseStatus status) {
		//建立响应对象
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, Unpooled.copiedBuffer("Failure:"+status.toString(),CharsetUtil.UTF_8));
		response.headers().set("Content-Type","text/plain;charset=UTF-8");
		ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
	}
	
	public static void setContentTypeHeader(HttpResponse response, File file) {
		//使用mime对象获取文件类型
		MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
		response.headers().set("Content-Type",mimeTypesMap.getContentType(file.getPath()));
	}
	
	//重定向操作
	public static void sendRedirect(ChannelHandlerContext ctx, String newUri) {
		//建立响应对象
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.FOUND);
		//设置新的请求放入响应对象中
		response.headers().set("Location", newUri);
		ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
	}
}
