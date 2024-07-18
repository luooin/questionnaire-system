package com.ming.questionnaire.controller;

import com.ming.questionnaire.interfaces.AccessLimit;
import com.ming.questionnaire.pojo.LoginUser;
import com.ming.questionnaire.pojo.ResponseResult;
import com.ming.questionnaire.pojo.views.SimplePaper;
import com.ming.questionnaire.pojo.views.AddViewPaper;
import com.ming.questionnaire.service.AnswerService;
import com.ming.questionnaire.service.PaperService;
import com.ming.questionnaire.service.QuestionService;
import com.ming.questionnaire.service.UserService;
import com.ming.questionnaire.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/paper")
public class PaperController {

    @Autowired
    private PaperService paperService;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private AnswerService answerService;
    @Autowired
    private UserService userService;

    // 添加或者修改一个问卷
    @PostMapping("/addPaper")
    public ResponseResult addPaper(@RequestBody AddViewPaper reqPaper,HttpServletRequest request){
        // 判断用户有没有绑定邮箱，如果没有绑定邮箱，返回信息提醒用户绑定邮箱
        // 获取token
        String token = request.getHeader("token");
        // 解析token
        String userId = "";
        try {
            userId = JwtUtil.parseJWT(token).getSubject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 通过userId查询用户邮箱
        // 如果查询到的email为null，说明用户没有绑定邮箱，直接返回
        if (StringUtils.isEmpty(userService.getEmailById(userId))){
            return new ResponseResult(401,"检测到你还没有绑定邮箱，请先绑定邮箱后再发布问卷");
        }

        // 从SecurityContextHolder.getContext()中获取用户信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        // 创建一个页面问卷，用来将问卷和问题绑定到一起
        AddViewPaper addViewPaper = new AddViewPaper();
        addViewPaper.setPaperId(reqPaper.getPaperId());
        addViewPaper.setUserId(loginUser.getUser().getUserId());
        addViewPaper.setPaperTitle(reqPaper.getPaperTitle());  // 设置问卷标题
        addViewPaper.setPaperIntroduce(reqPaper.getPaperIntroduce());  // 设置问卷简介
        addViewPaper.setStatus(reqPaper.getStatus());              // 问卷可以填写
        addViewPaper.setQuestionList(reqPaper.getQuestionList()); // 设置问题
        // TODO 设置开始时间和结束时间
        // 调用service 将用户设置的问题分解成问题表和问卷表，分别存入数据库中
        int i = paperService.addOrUpdatePaper(addViewPaper);  // 因为返回值都是自定义的返回结果集，这里直接返回
        if (i == 1) {
            return new ResponseResult(200,"更新问卷成功");
        }else if(i==2){
            return new ResponseResult(200,"添加问卷成功");
        }else {
            return new ResponseResult(500,"发生错误，请稍后再试");
        }
    }

    // 获取登录用户创建的问卷
    @GetMapping("/getMyPaper")
    public ResponseResult<List<SimplePaper>> getMyPaper(){
        // 获取当前登录用户的信息
        // 从SecurityContextHolder.getContext()中获取用户信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        String userId = loginUser.getUser().getUserId();  // 获取userid
        List<SimplePaper> allPaper = paperService.getSimplePaperByUId(userId);   // 通过用户id查找所有问卷

        return new ResponseResult<List<SimplePaper>>(200,"获取问卷成功",allPaper);
    }

    // 通过id删除一个问卷
    @PostMapping("/delPaperById")
    public ResponseResult delPaperById(@RequestBody String paperId){
        boolean isDel = paperService.deletePaperById(paperId);
        if (isDel){
            return new ResponseResult(200,"删除问卷成功");
        }else {
            return new ResponseResult(500,"删除问卷失败");
        }
    }

    // 通过id查询一个问卷
    @GetMapping("/getPaperById/{paperId}")
    @AccessLimit(seconds = 3,maxCount = 5)   // 自定义的注解，一秒钟不能连续请求两次
    public ResponseResult selectPaperById(@PathVariable("paperId") String paperId){
        AddViewPaper addViewPaper = paperService.selectPaperById(paperId);
        // 封装一个viewPaper对象返回给前端
        if (addViewPaper !=null){
            return new ResponseResult(200,"查询成功", addViewPaper);
        }else {
            return new ResponseResult(500,"服务器错误，查询失败");
        }
    }

    // 查询一个问卷是否有人填写过
    @GetMapping("/paperIsAnswered/{paperId}")
    public ResponseResult<Boolean> paperIsAnswered(@PathVariable("paperId")String paperId){
        // 判断问卷是否存在
        int paperCount = paperService.getPaperCount(paperId);
        if (paperCount==0){
            throw new RuntimeException("问卷id有问题");
        }
        int count = answerService.getCountByPId(paperId);
        if (count>0){
            return new ResponseResult<Boolean>(400,"这个问卷有用户进行填写，如果修改，填写信息也会丢失，确定还要修改吗？",true);
        }else {
            return new ResponseResult<Boolean>(200,"可以修改问卷",false);
        }
    }

    // 判断一个问卷是否被封禁
    @GetMapping("/paperIsBan/{paperId}")
    public ResponseResult<Boolean> paperIsBan(@PathVariable("paperId")String paperId){
        // 判断问卷是否存在
        int paperCount = paperService.getPaperCount(paperId);
        if (paperCount==0){
            throw new RuntimeException("问卷id有问题");
        }
        int paperStatus = paperService.selectPaperStatus(paperId);
        if (paperStatus==-1){
            return new ResponseResult<Boolean>(400,"该问卷已经被封禁，请联系管理员解除封禁",true);
        }else {
            return new ResponseResult<Boolean>(200,"问卷没有被封禁",false);
        }
    }

    // 查询一个问卷状态，用户是否有资格访问
    @GetMapping("/queryPState/{paperId}")
    public ResponseResult queryPState(@PathVariable("paperId")String paperId, HttpServletRequest request){
        // i代表状态，如果是1，证明问卷可以成功访问（可能是问卷已经发布，也可能是问卷未发布但是问卷的发布者查看问卷）
        // 如果是0说明问卷未发布，如果是-1说明问卷已封禁
        int i = paperService.queryPStateById(paperId,request);
        if (i==1){
            return new ResponseResult(200,"查询成功,可以填写答卷");
        }else if (i==0){
            return new ResponseResult(401,"问卷未发布，请发布后再填写");
        }else {
            return new ResponseResult(401,"问卷已被封禁");
        }
    }

    // 发布一个问卷
    @PostMapping("/issuePaper")
    public ResponseResult issuePaper(@RequestBody String paperId){
        int i = paperService.issuePaper(paperId);
        if (i>0){
            return new ResponseResult(200,"发布问卷成功",1); // data为paper的state
        }else {
            return new ResponseResult(401,"发布问卷失败",0);
        }
    }

    // 取消问卷发布
    @PostMapping("/cancelIssuePaper")
    public ResponseResult cancelIssuePaper(@RequestBody String paperId){
        int i = paperService.cancelIssuePaper(paperId);
        if (i>0){
            return new ResponseResult(200,"成功取消发布",0); // data为paper的state
        }else {
            return new ResponseResult(401,"取消发布问卷失败",1);
        }
    }

}
