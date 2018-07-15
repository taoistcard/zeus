package zeus.server.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import zeus.network.handler.IBusinessHandler;
import zeus.network.handler.IntranetTcpChannelInitializer;
import zeus.network.threading.ITaskPool;

/**
 * ƒÈÂ“¾Wtcp·þ„ÕÅäÖÃ
 * @author songfeilong
 *
 */
@Component
@ConfigurationProperties(prefix = "server.intranet")
public class IntranetTcpServerConfig extends BaseTcpServerConfig {

	private IBusinessHandler bunisnessHandler;
	private ITaskPool taskPool;

	public void setBunisnessHandler(IBusinessHandler bunisnessHandler,ITaskPool taskPool) {
		this.bunisnessHandler = bunisnessHandler;
		this.taskPool = taskPool;
	}

	public void init() {
		super.init();
		handler = new LoggingHandler(LogLevel.DEBUG);
		childHandler = new IntranetTcpChannelInitializer(bunisnessHandler, taskPool);
	}
}
