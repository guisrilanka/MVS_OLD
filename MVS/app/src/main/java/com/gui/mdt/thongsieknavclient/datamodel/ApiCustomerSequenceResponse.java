package com.gui.mdt.thongsieknavclient.datamodel;

import java.util.List;

public class ApiCustomerSequenceResponse {
    private String status;
    private List<ApiCustomer> routeSequenceMvs;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<ApiCustomer> getRouteSequenceMvs() {
        return routeSequenceMvs;
    }

    public void setRouteSequenceMvs(List<ApiCustomer> routeSequenceMvs) {
        this.routeSequenceMvs = routeSequenceMvs;
    }

    public class ApiCustomer{
        private String customer_code;

        public String getCustomer_code() {
            return customer_code;
        }

        public void setCustomer_code(String customer_code) {
            this.customer_code = customer_code;
        }
    }
}

