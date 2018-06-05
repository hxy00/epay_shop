package com.emt.shoptask.sv.inter;

public interface IPaySv {

    public void alreadyPayPost();

    public void unPayQuery(int orderCloseTime, String sysId);

}
