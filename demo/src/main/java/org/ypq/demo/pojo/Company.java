package org.ypq.demo.pojo;

import java.math.BigDecimal;

public class Company {

    private String companyName;
    private BigDecimal asset;

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public BigDecimal getAsset() {
        return asset;
    }

    public void setAsset(BigDecimal asset) {
        this.asset = asset;
    }
}
