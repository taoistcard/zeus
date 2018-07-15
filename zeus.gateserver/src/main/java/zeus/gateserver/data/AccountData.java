package zeus.gateserver.data;

import org.springframework.stereotype.Component;

@Component
public class AccountData {
//	@Autowired
//	JedisUtils jedisUtils;
//	@Autowired
//	AccountPriDao accountPriDao;
//	@Autowired
//	AccountPropertyPriDao accountPropertyPriDao;
//	@Autowired
//	WalletPriDao walletDao;
//	@Autowired
//	AnchorContractPriDao anchorContractPriDao;

//	public AccountUtils(JedisUtils jedisUtils,
//			AccountPriDao accountPriDao, 
//			AccountPropertyPriDao accountPropertyPriDao,
//			WalletPriDao walletDao,
//			AnchorContractPriDao anchorContractPriDao){
//		this.jedisUtils = jedisUtils;
//		this.accountPriDao = accountPriDao;
//		this.accountPropertyPriDao = accountPropertyPriDao;
//		this.walletDao = walletDao;
//		this.anchorContractPriDao = anchorContractPriDao;
//	}
	/**
	 * 获取token
	 * 
	 * @author frank
	 * @param accId
	 * @return
	 */
	public String getToken(String accId) {
//		if (!StringUtils.StringIsEmptyOrNull(accId) && jedisUtils.exist(Constant.HOT_ACCOUNT_TOKEN_SET_KEY + accId)) {
//			return jedisUtils.get(Constant.HOT_ACCOUNT_TOKEN_SET_KEY + accId);
//		}
		return null;
	}

//	
//	public static int getRichScoreLevel(long score) {
//		score = score / 100;
//		if (score <= 10) {
//			return 1;
//		}
//		if (score <= 100) {
//			return 2;
//		}
//		if (score <= 200) {
//			return 3;
//		}
//		if (score <= 500) {
//			return 4;
//		}
//		if (score <= 800) {
//			return 5;
//		}
//		if (score <= 2000) {
//			return 6;
//		}
//		if (score <= 5000) {
//			return 7;
//		}
//		if (score <= 10000) {
//			return 8;
//		}
//		if (score <= 20000) {
//			return 9;
//		}
//		if (score <= 50000) {
//			return 10;
//		}
//		if (score <= 100000) {
//			return 11;
//		}
//		if (score <= 200000) {
//			return 12;
//		}
//		if (score <= 300000) {
//			return 13;
//		}
//		if (score <= 400000) {
//			return 14;
//		}
//		if (score <= 500000) {
//			return 15;
//		}
//		if (score <= 600000) {
//			return 16;
//		}
//		if (score <= 800000) {
//			return 17;
//		}
//		if (score <= 1000000) {
//			return 18;
//		}
//		if (score <= 2000000) {
//			return 19;
//		}
//		if (score <= 3000000) {
//			return 20;
//		}
//		return 20;
//	}
}
