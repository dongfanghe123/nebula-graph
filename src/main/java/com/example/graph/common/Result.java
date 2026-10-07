package com.example.graph.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result {
    private Integer code;
    private String message;
    private Object data;

    public static Result success(Object data){
        return new Result(200,"请求成功",data);
    }

    public static Result success(){
        return new Result(200,"请求成功",null);
    }

    public static Result fail(){
        return new Result(500,"请求失败",null);
    }

    public static Result fail(String message){
        return new Result(500,message,null);
    }

    public static Result fail(Integer code,String message){
        return new Result(code,message,null);
    }
}
