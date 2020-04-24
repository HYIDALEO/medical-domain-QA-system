package com.appleyk.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import javax.websocket.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.appleyk.service.QAService;
import com.appleyk.service.QuestionService;

import net.sf.json.JSONObject;

//import net.sf.json.JSONObject;

@RestController
@RequestMapping("/rest/appleyk/question")
public class QuestionController {
    private static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static Vector<Session> room = new Vector<>();
	@Autowired
	QAService qaService;
	
	@RequestMapping("/query")
	public String query(@RequestParam(value = "question") String question) throws Exception {
//		System.out.println(questService.answer(question));
		System.out.println(question);
		
        JSONObject obj = new JSONObject();
        obj.put("nickname","机器人");
        obj.put("date",df.format(new Date()));
        obj.put("isSelf", false);
        
        
        
        
        JSONObject obj_realAn = new JSONObject();
        obj_realAn.put("date", df.format(new Date()));
        obj_realAn.put("isSelf", true);
        String input = question;
        input = input.replaceAll("&nbsp;", "");
        input = input.replaceAll("<p>", "");
        input = input.replaceAll("</p>", "");
        input = input.replaceAll("<br/>", "");
        String result =  qaService.answer(input);
        
        obj.put("content","问："+input+"<br/>"+"答："+result);
        
        String ret = obj.toString();
		return ret;
	}
	
	@RequestMapping("/path")
	public void checkPath(){
		qaService.showDictPath();
	}
}
