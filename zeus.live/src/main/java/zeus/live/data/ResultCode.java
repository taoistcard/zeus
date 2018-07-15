package zeus.live.data;


/**
 * Define common result code
 * 
 * @author frank
 *
 */
public enum ResultCode {
	SUCCESS("success"), SERVICE_ERROR("service_error"), PARAM_INVALID("param_invalid"), UPDATE_NOW_GIFT_CONFIG(
			"update_now_gift_config"), IGOLD_NOT_ENOUGH(
					"igold_not_enough"), TOKEN_INVALID("token_invalid"), AUTH("no_auth"), DIFF_SPACE_LOGIN("diff_space_login");

	private String code;

	private ResultCode(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}
