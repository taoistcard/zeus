package zeus.gateserver.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.i5i58.secondary.dao.account.AccountSecDao;
import com.i5i58.secondary.dao.channel.ChannelSecDao;

@Component
public class SecDaoDelegate {
	@Autowired
    private ChannelSecDao channelSecDao;
	
	@Autowired
	private AccountSecDao accountSecDao;
    
    public ChannelSecDao getChannelDao(){
    	return channelSecDao;
    }

	public AccountSecDao getAccountSecDao() {
		return accountSecDao;
	}
}
