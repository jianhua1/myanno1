package controller;

import anno.Controller;
import anno.RequestMapping;
import util.ScanClassUtil;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AnnotationHandleServlet extends HttpServlet {

    Map map=new HashMap<String,String>();

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        String basePackage = config.getInitParameter("basePackage");
        if(basePackage.indexOf(",")>0){
            String[] basePackageArr = basePackage.split(",");
            for (String s : basePackageArr) {
                Set<Class> classes = ScanClassUtil.getClasses(s);
                for (Class aClass : classes) {
                    if (aClass.isAnnotationPresent(Controller.class)){
                        Method[] declaredMethods = aClass.getDeclaredMethods();
                        for (Method declaredMethod : declaredMethods) {
                            if(declaredMethod.isAnnotationPresent(RequestMapping.class)){
                                String value = declaredMethod.getAnnotation(RequestMapping.class).value();
                                System.out.println(value);
                                if(value!=null && value!=""){
                                    if(map.containsKey(value)){
                                       throw new RuntimeException("RequestMapping映射的地址不允许重复！");
                                    }
                                    map.put(value,aClass);
                                }
                            }
                        }
                    }
                }
            }
        }


    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.service(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.service(req,resp);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)  {
        try{
            String path = req.getContextPath()+"/";
            String requestUri = req.getRequestURI();
            String midUrl = requestUri.replaceFirst(path, "");
            String lasturl = midUrl.substring(0, midUrl.lastIndexOf("."));
            Class o = (Class)map.get(lasturl);
            Object o1 = o.newInstance();
            Method[] declaredMethods = o.getDeclaredMethods();
            for (Method declaredMethod : declaredMethods) {
               if(declaredMethod.isAnnotationPresent(RequestMapping.class)){
                   String value = declaredMethod.getAnnotation(RequestMapping.class).value();
                   if(value!=null && lasturl.equals(value)){
                       String invoke = (String)declaredMethod.invoke(o1);
                       req.getRequestDispatcher(invoke).forward(req,resp);
                   }
               }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
