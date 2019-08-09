package controller;

import anno.Controller;
import anno.RequestMapping;

import javax.faces.annotation.RequestMap;


@Controller
public class Ab {
    @RequestMapping("abc")
   public String getA(){
       System.out.println("ccc");
       return "abc.html";
   }
}
