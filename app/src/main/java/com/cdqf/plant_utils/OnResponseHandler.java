package com.cdqf.plant_utils;

/**
 * ��Ӧ������
 * @author �Ƽ������޺�
 *
 */
public interface OnResponseHandler {
	
	/**
	 * �����Ӧ���
	 * @param result ��Ӧ����
	 * @param status ״̬
	 */
	void onResponse(String result, RequestStatus status);

}
