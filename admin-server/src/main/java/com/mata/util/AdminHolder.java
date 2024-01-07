package com.mata.util;


import com.mata.dto.AdminDto;

public class AdminHolder {
    private static final ThreadLocal<AdminDto> tl = new ThreadLocal<>();

    public static void saveAdmin(AdminDto admin){
        tl.set(admin);
    }

    public static AdminDto getAdmin(){
        return tl.get();
    }

    public static void removeAdmin(){
        tl.remove();
    }
}
