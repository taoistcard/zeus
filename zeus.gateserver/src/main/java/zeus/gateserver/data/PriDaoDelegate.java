package zeus.gateserver.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.i5i58.primary.dao.channel.ChannelPriDao;

@Component
public class PriDaoDelegate {

    @Autowired
    private ChannelPriDao channelPriDao;
    
    public ChannelPriDao getChannelDao(){
    	return channelPriDao;
    }
}
