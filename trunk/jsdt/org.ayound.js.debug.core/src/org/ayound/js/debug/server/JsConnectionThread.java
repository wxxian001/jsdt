/*******************************************************************************
 *
 *==============================================================================
 *
 * Copyright (c) 2008-2011 ayound@gmail.com
 * All rights reserved.
 * 
 * Created on 2008-10-26
 *******************************************************************************/
package org.ayound.js.debug.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.StringTokenizer;

import org.eclipse.debug.core.model.IThread;

public class JsConnectionThread extends Thread {
	private Socket connection;

	private IThread thread;

	private IDebugServer server;

	public JsConnectionThread(Socket conn, IThread thread, IDebugServer server) {
		this.connection = conn;
		this.thread = thread;
		this.server = server;
		this.start();
	}

	@Override
	public void run() {
		try {
			int contentLength = 0;// 客户端发送的 HTTP 请求的主体的长度
			if (this.connection != null) {
				try {
					// 第一阶段: 打开输入流
					BufferedReader in = new BufferedReader(
							new InputStreamReader(this.connection
									.getInputStream()));

					// 读取第一行, 请求地址
					String line = in.readLine();
					String resource = line.substring(line.indexOf('/'), line
							.lastIndexOf('/') - 5);
					// 获得请求的资源的地址
					resource = URLDecoder.decode(resource, "UTF-8");// 反编码
					// URL
					// 地址
					String method = new StringTokenizer(line).nextElement()
							.toString();// 获取请求方法, GET 或者 POST

					// 读取所有浏览器发送过来的请求参数头部信息
					while ((line = in.readLine()) != null) {

						// 读取 POST 等数据的内容长度
						if (line.startsWith("Content-Length")) {
							try {
								contentLength = Integer.parseInt(line
										.substring(line.indexOf(':') + 1)
										.trim());
							} catch (Exception e) {
								e.printStackTrace();
							}
						}

						if (line.equals("")) {
							break;
						}
					}
					StringBuffer buffer = new StringBuffer();
					// 显示 POST 表单提交的内容, 这个内容位于请求的主体部分
					if ("POST".equalsIgnoreCase(method) && (contentLength > 0)) {
						for (int i = 0; i < contentLength; i++) {
							buffer.append((char) in.read());
						}
					}

					JsDebugResponse response = new JsDebugResponse(
							this.connection.getOutputStream(), this.connection,this.server.getJsResourceManager());
					IServerProcessor processor = ProcessorFactory
							.createProcessor(resource, method, buffer
									.toString(), response, this.thread,
									this.server);
					processor.process();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			// System.out.println(connection+"连接到HTTP服务器");//如果加入这一句,服务器响应速度会很慢
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public IDebugServer getServer() {
		return this.server;
	}
}
