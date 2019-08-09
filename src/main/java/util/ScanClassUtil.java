package util;

import java.io.File;
import java.io.FileFilter;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

public class ScanClassUtil {
   public static Set getClasses(String pack){
       Set set=new HashSet();
       try{
           String packageDirName=pack.replace(".","/");
           Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
           while (resources.hasMoreElements()){
               URL url = resources.nextElement();
               String protocol = url.getProtocol();
               if("file".equals(protocol)){
                   String decode = URLDecoder.decode(url.getFile(), "utf-8");
                   System.out.println(decode);
                   File file=new File(decode);
                   File[] files = file.listFiles(new FileFilter() {
                       public boolean accept(File file) {
                           return file.getName().endsWith(".class");
                       }
                   });
                   for (File file1 : files) {
                       String substring = file1.getName().substring(0, file1.getName().length() - 6);
                       Class<?> aClass = Thread.currentThread().getContextClassLoader().loadClass(pack+"."+substring);
                       set.add(aClass);
                   }
               }
           }
       }catch (Exception e){
           e.printStackTrace();
       }
       return set;
   }

    public static void main(String[] args) {
        String packName="controller";
        Set classes = ScanClassUtil.getClasses(packName);
        //System.out.println(classes.toString());
    }
}
