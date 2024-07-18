package com.ming.questionnaire.controller;

import com.ming.questionnaire.pojo.ResponseResult;
import com.ming.questionnaire.pojo.User;
import com.ming.questionnaire.pojo.views.admin.QueryInfo;
import com.ming.questionnaire.pojo.views.admin.ViewPaperInfo;
import com.ming.questionnaire.pojo.views.admin.ViewUserInfo;
import com.ming.questionnaire.service.AdminService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PostMapping("/login")
    public ResponseResult login(@RequestBody User user){
        return adminService.adminLogin(user);
    }

    // 获取用户近七天登录情况
    @GetMapping("/getUserCount")
    @PreAuthorize("hasAnyAuthority('sys::admin')")   // 需要对应权限才可以访问这个接口
    public ResponseResult<List<Integer>> getUserCount(){
        Map<String, Object> map = adminService.getUserLoginCount();
        return new ResponseResult(200,"查询七天内登录人数成功",map);
    }
    // 获取用户近七天问卷创建情况
    @GetMapping("/getPaperCount")
    @PreAuthorize("hasAnyAuthority('sys::admin')")   // 需要对应权限才可以访问这个接口
    public ResponseResult<List<Integer>> getPaperCount(){
        Map<String, Object> map = adminService.getPaperReleaseCount();  // 获取问卷发布情况
        return new ResponseResult(200,"查询七天内发布问卷人数成功",map);
    }
    // 获取用户近七天问卷创建情况
    @GetMapping("/getAnswerCount")
    @PreAuthorize("hasAnyAuthority('sys::admin')")   // 需要对应权限才可以访问这个接口
    public ResponseResult<List<Integer>> getAnswerCount(){
        Map<String, Object> map = adminService.getAnswerReleaseCount();  // 获取问卷发布情况
        return new ResponseResult(200,"查询七天内回答问卷人数成功",map);
    }

    // 分页获取用户信息
    @GetMapping("/getUsers")
    @PreAuthorize("hasAnyAuthority('sys::admin')")   // 需要对应权限才可以访问这个接口
    public ResponseResult getUsers(@Param(value = "params") QueryInfo queryInfo){
        List<ViewUserInfo> userList = adminService.getUserList(queryInfo);
        // 查询一共有多少个用户
        int total = adminService.getUserCount(queryInfo.getQuery());
        Map<String, Object> map = new HashMap<>();
        map.put("users",userList);
        map.put("total",total);  // 查询到了多少条数据
        return new ResponseResult(200,"查询成功",map);
    }

    // 封禁或者解封一个用户
    @PostMapping("/updateState")
    @PreAuthorize("hasAnyAuthority('sys::admin')")
    public ResponseResult updateState(@RequestBody Map<String ,Object> map){
        String userId = (String) map.get("userId");
        int state = (int) map.get("state");
        int i = adminService.updateStateById(userId, state);
        if (i>0){
            return new ResponseResult(200,"更新用户状态成功");
        }else {
            return new ResponseResult(401,"更新用户状态失败,请稍后再试");
        }
    }

    // 分页获取问卷信息
    @GetMapping("/getPapers")
    @PreAuthorize("hasAnyAuthority('sys::admin')")
    public ResponseResult<Map<String,Object>> getPapers(@Param(value = "params") QueryInfo queryInfo){
        List<ViewPaperInfo> paperList = adminService.getPaperList(queryInfo);
        HashMap<String, Object> map = new HashMap<>();
        int total = adminService.getPaperCount(queryInfo.getQuery());
        map.put("paperList",paperList);
        map.put("total",total);
        return new ResponseResult<>(200,"查询成功",map);
    }

    // 封禁一个问卷
    @PostMapping("/banPaper")
    @PreAuthorize("hasAnyAuthority('sys::admin')")
    public ResponseResult banPaper(@RequestBody String paperId){
        int i = adminService.banPaperById(paperId);
        if (i>0){
            return new ResponseResult(200,"封禁问卷成功");
        }else {
            return new ResponseResult(401,"封禁问卷失败");
        }
    }
    // 取消一个问卷的封禁
    @PostMapping("/noBanPaper")
    @PreAuthorize("hasAnyAuthority('sys::admin')")
    public ResponseResult noBanPaper(@RequestBody String paperId){
        int i = adminService.noBanPaperById(paperId);
        if (i>0){
            return new ResponseResult(200,"解除封禁成功");
        }else {
            return new ResponseResult(401,"解除封禁失败,请稍后再试");
        }
    }


}
