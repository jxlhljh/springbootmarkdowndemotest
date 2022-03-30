package cn.gzsendi.web.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import cn.gzsendi.web.model.Article;
import cn.gzsendi.web.service.ArticleService;
import cn.gzsendi.web.service.impl.ArticleServiceImpl;

@Controller
public class ArticleController {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Value("${localFiles.absoluteFilePath}")
	private String imageAbsoluteFilePath;
	
	public static final String imageUploadPathPrex = ArticleServiceImpl.imageUploadPathPrex;//临时图片目录的一部分名称，将与imageAbsoluteFilePath组成临时目录的前缀
	public static final String tempPrex = ArticleServiceImpl.tempPrex; 
	
	@Autowired
	private ArticleService articleService;
	
	//文章列表数据
    @GetMapping("/getArticleListData")
    @ResponseBody
    public List<Article> getArticleListData(){
    	return articleService.getArticleList();
    }
    
	//文章数据
    @GetMapping("/getArticleById")
    @ResponseBody
    public Article getArticleById(HttpServletRequest request){
        String id = request.getParameter("id");
        Assert.notNull(id, "文章ID不能为空");
    	return articleService.getArticleById(Integer.parseInt(id));
    }
    
	//更新文章
    @PostMapping("/updateArticle")
    @ResponseBody
    public Article updateArticle(@RequestBody Article article){
    	int id = article.getId();
    	if(id == 0) throw new RuntimeException("文章ID不能为空");
    	articleService.updateArticle(article);
    	return article;
    }
    
	//新加文章
    @PostMapping("/addArticle")
    @ResponseBody
    public Article addArticle(@RequestBody Article article){
    	articleService.addArticle(article);
    	return article;
    }
    
    //删除文章
    @PostMapping("/deleteArticle")
    @ResponseBody
    public Article deleteArticle(@RequestBody Article article){
    	int id = article.getId();
    	if(id == 0) throw new RuntimeException("文章ID不能为空");
    	articleService.deleteArticle(article);
    	return article;
    }
    
	//临时照片展示
    @GetMapping("/getTempImage")
    public void getTempImage(HttpServletRequest request, HttpServletResponse response) throws IOException{
    	
    	String fileName = request.getParameter("fileName");
    	if("".equals(fileName) || fileName==null){
    		throw new RuntimeException("fileName参数不能为空");
    	}
    	
    	// 图片的路径+文件名称
        fileName = "/" + imageUploadPathPrex + "/" + tempPrex + "/" + fileName;
    	File file = new File(imageAbsoluteFilePath, fileName);
    	if(!file.exists()){
    		throw new RuntimeException("对应的图片找不到，fileName: " + fileName);
    	}
    	
    	FileInputStream fin = null;
    	OutputStream os = null;
        try {
			fin = new FileInputStream(file);
			byte[] bytes = new byte[fin.available()];
			fin.read(bytes, 0, fin.available());
			fin.close();
			response.setContentType(MediaType.IMAGE_JPEG_VALUE);
			os = response.getOutputStream();  
			os.write(bytes);  
			os.flush();  
		} catch (Exception e) {
			logger.error("error",e);
		} finally {
			fin.close();
			os.close();
		}
    	
    }
    
	//正式照片展示，md上传的新照片，是先以/getTempImage?fileName=xxx的方式临时存放
    //发表文章或修改文章时，通过以文章Id为目录将文章的图片独立出去管理，
    //正式的照片访问url如：/getImage?id=10&fileName=xxxxxxxxxxxx.jpg
    @GetMapping("/getImage")
    public void getImage(HttpServletRequest request, HttpServletResponse response) throws IOException{
    	
    	String fileName = request.getParameter("fileName");
    	if("".equals(fileName) || fileName==null){
    		throw new RuntimeException("fileName参数不能为空");
    	}
    	
    	String id = request.getParameter("id");
    	if("".equals(id) || id==null){
    		throw new RuntimeException("id参数不能为空");
    	}
    	
    	// 图片的路径+文件名称
        fileName = "/" + imageUploadPathPrex + "/" +id+"/" + fileName;
    	File file = new File(imageAbsoluteFilePath, fileName);
    	
    	if(!file.exists()){
    		throw new RuntimeException("对应的图片找不到，fileName: " + fileName);
    	}
    	
    	FileInputStream fin = null;
    	OutputStream os = null;
        try {
			fin = new FileInputStream(file);
			byte[] bytes = new byte[fin.available()];
			fin.read(bytes, 0, fin.available());
			fin.close();
			response.setContentType(MediaType.IMAGE_JPEG_VALUE);
			os = response.getOutputStream();  
			os.write(bytes);  
			os.flush();  
		} catch (Exception e) {
			logger.error("error",e);
		} finally {
			fin.close();
			os.close();
		}
    }
    
    //上传图片并回显
    @ResponseBody
    @RequestMapping("/article/uploadImg")
    public Map<String,Object> uploadImg(HttpServletRequest request, @RequestParam(value = "editormd-image-file", required = false) MultipartFile file){
        Map<String,Object> map = new HashMap<>();
        if (file != null){
        	
            try{
                //获取文件名
                String filename = file.getOriginalFilename();
                UUID uuid = UUID.randomUUID();
                String name = "";
                if (filename != null){
                    name = filename.substring(filename.lastIndexOf(".")); //获取文件后缀名
                }
                
                // 图片的路径+文件名称
                String fileName = "/" + imageUploadPathPrex + "/" + tempPrex + "/" + uuid + name;
                
                // 图片的在服务器上面的物理路径
                File destFile = new File(imageAbsoluteFilePath, fileName);
                
                // 生成upload目录
                File parentFile = destFile.getParentFile();
                if (!parentFile.exists()) {
                    parentFile.mkdirs();// 自动生成upload目录
                }
                
                // 把上传的临时图片，复制到当前项目的webapp路径
                FileCopyUtils.copy(file.getInputStream(), new FileOutputStream(destFile));
                map.put("success",1); //设置回显的数据 0 表示上传失败，1 表示上传成功
                map.put("message","上传成功"); //提示的信息，上传成功或上传失败及错误信息等
                
                String contextPath = request.getContextPath();
                map.put("url",contextPath + "/getTempImage?fileName="+ uuid + name); //图片回显的url 上传成功时才返回
                
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return map;
    }
    

}
