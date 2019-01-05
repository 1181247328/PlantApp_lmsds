package com.cdqf.plant_class;

/**
 * 用户id
 * Created by XinAiXiaoWen on 2017/4/19.
 */

public class LogUser {
    //用户id
    private int id;

    //登录用户名
    private String userName = null;

    //头像
    private String avatar = null;

    //用户昵称
    private String nickName = null;

    //真实名字
    private String realName = null;

    //家庭住址
    private String homeAddress = "";

    //单位住址
    private String companyAddress = null;

    //固定电话
    private String tel = null;

    //手机号码
    private String mobile = null;

    //电子箱邮
    private String email = null;

    //性别
    private int sex;

    //生日
    private String birth = null;

    //会员号
    private String njiaVipNo = null;

    //入会时间
    private String vipDate = null;

    //会员等级
    private int vipLevel;

    //证件号码
    private String certificatesNo = null;

    //证件类型表id
    private int libCertificatesCateId;

    //证件类型
    private String libCertificatesCateDesc = null;

    //所在地区id
    private int areaRegionId;

    //所在地区
    private String areaRegionDesc;

    //常驻城市地区ID
    private int livingCity;

    //常驻城市地区
    private String livingCityDesc;

    //注册时间
    private String RegTime;

    //是否验证手机
    private boolean isValidateMobile;

    //是否验证邮箱
    private boolean isValidatedEmail;

    //是否自动注册
    private boolean isAutoRegister;

    //密码度    1低 2中 3高
    private int passWordStrength;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(String homeAddress) {
        this.homeAddress = homeAddress;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getNjiaVipNo() {
        return njiaVipNo;
    }

    public void setNjiaVipNo(String njiaVipNo) {
        this.njiaVipNo = njiaVipNo;
    }

    public String getVipDate() {
        return vipDate;
    }

    public void setVipDate(String vipDate) {
        this.vipDate = vipDate;
    }

    public int getVipLevel() {
        return vipLevel;
    }

    public void setVipLevel(int vipLevel) {
        this.vipLevel = vipLevel;
    }

    public String getCertificatesNo() {
        return certificatesNo;
    }

    public void setCertificatesNo(String certificatesNo) {
        this.certificatesNo = certificatesNo;
    }

    public int getLibCertificatesCateId() {
        return libCertificatesCateId;
    }

    public void setLibCertificatesCateId(int libCertificatesCateId) {
        this.libCertificatesCateId = libCertificatesCateId;
    }

    public String getLibCertificatesCateDesc() {
        return libCertificatesCateDesc;
    }

    public void setLibCertificatesCateDesc(String libCertificatesCateDesc) {
        this.libCertificatesCateDesc = libCertificatesCateDesc;
    }

    public int getAreaRegionId() {
        return areaRegionId;
    }

    public void setAreaRegionId(int areaRegionId) {
        this.areaRegionId = areaRegionId;
    }

    public String getAreaRegionDesc() {
        return areaRegionDesc;
    }

    public void setAreaRegionDesc(String areaRegionDesc) {
        this.areaRegionDesc = areaRegionDesc;
    }

    public int getLivingCity() {
        return livingCity;
    }

    public void setLivingCity(int livingCity) {
        this.livingCity = livingCity;
    }

    public String getLivingCityDesc() {
        return livingCityDesc;
    }

    public void setLivingCityDesc(String livingCityDesc) {
        this.livingCityDesc = livingCityDesc;
    }

    public String getRegTime() {
        return RegTime;
    }

    public void setRegTime(String regTime) {
        RegTime = regTime;
    }

    public boolean isValidateMobile() {
        return isValidateMobile;
    }

    public void setValidateMobile(boolean validateMobile) {
        isValidateMobile = validateMobile;
    }

    public boolean isValidatedEmail() {
        return isValidatedEmail;
    }

    public void setValidatedEmail(boolean validatedEmail) {
        isValidatedEmail = validatedEmail;
    }

    public boolean isAutoRegister() {
        return isAutoRegister;
    }

    public void setAutoRegister(boolean autoRegister) {
        isAutoRegister = autoRegister;
    }

    public int getPassWordStrength() {
        return passWordStrength;
    }

    public void setPassWordStrength(int passWordStrength) {
        this.passWordStrength = passWordStrength;
    }
}
