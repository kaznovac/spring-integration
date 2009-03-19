/*
 * Copyright 2002-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.integration.http;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;

import org.springframework.integration.core.Message;
import org.springframework.integration.handler.AbstractReplyProducingMessageHandler;
import org.springframework.integration.handler.ReplyMessageHolder;
import org.springframework.integration.message.MessageHandlingException;
import org.springframework.util.FileCopyUtils;

/**
 * An outbound endpoint for executing an HTTP request and mapping the response
 * to a reply Message.
 * 
 * @author Mark Fisher
 * @since 1.0.2
 */
public class HttpOutboundEndpoint extends AbstractReplyProducingMessageHandler {

	private final OutboundRequestMapper requestMapper;

	private volatile HttpRequestExecutor requestExecutor;


	public HttpOutboundEndpoint(URL url) {
		this.requestMapper = new DefaultOutboundRequestMapper(url);
		this.requestExecutor = new SimpleHttpRequestExecutor();
	}


	@Override
	protected void handleRequestMessage(Message<?> requestMessage, ReplyMessageHolder replyMessageHolder) {
		try {
			HttpRequest request = this.requestMapper.fromMessage(requestMessage);
			InputStream responseBody = this.requestExecutor.executeRequest(request);
			ByteArrayOutputStream responseByteStream = new ByteArrayOutputStream();
			FileCopyUtils.copy(responseBody, responseByteStream);
			replyMessageHolder.set(responseByteStream.toByteArray());
		}
		catch (Exception e) {
			throw new MessageHandlingException(requestMessage,
					"failed to execute HTTP request", e);
		}
	}

}
