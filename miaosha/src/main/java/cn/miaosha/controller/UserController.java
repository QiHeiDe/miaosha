package cn.miaosha.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.miaosha.mapper.UserMapper;
import cn.miaosha.pojo.User;
import cn.miaosha.pojo.UserExample;
import cn.miaosha.pojo.UserExample.Criteria;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserMapper userMapper;
	@RequestMapping("/toLogin")
	public String tologin(){
		return "login";
	}
	@RequestMapping("/login")
	public String login(HttpServletRequest request,HttpServletResponse response, User user,Model model) throws Exception{
		UserExample example = new UserExample();
		Criteria criteria = example.createCriteria();
		criteria.andUsernameEqualTo(user.getUsername());
		criteria.andPasswordEqualTo(user.getPassword());
		List<User> list = userMapper.selectByExample(example );
		if(list!=null&&list.size()>0){
			request.getSession().setAttribute("longUser", list.get(0));
			response.sendRedirect(request.getContextPath()+"/seckillGoods/findSeckillGoods.do");
			return null;
		}else{
			model.addAttribute("loginError", "账号密码错误！！");
			return "login";
		}
	}
}
