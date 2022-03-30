package cn.gzsendi.web.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.gzsendi.web.mapper.ArticleMapper;
import cn.gzsendi.web.model.Article;
import cn.gzsendi.web.service.ArticleService;

@Service
public class ArticleServiceImpl implements ArticleService {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public static final String imageUploadPathPrex = "upload";//临时图片目录的一部分名称，将与imageAbsoluteFilePath组成临时目录的前缀
	public static final String tempPrex = "temp"; 
	
	@Value("${localFiles.absoluteFilePath}")
	private String imageAbsoluteFilePath;
	
    @Resource
    private ArticleMapper articleMapper;

	@Override
	public List<Article> getArticleList() {
		return articleMapper.getArticleList();
	}

	@Override
	public Article getArticleById(Integer id) {
		return articleMapper.getArticleById(id);
	}



	@Override
	@Transactional
	public int addArticle(Article article) {
		
		try {
			//先插入数据库
			articleMapper.addArticle(article);
			
			//取出自增id
			int id = article.getId();
			
			//将文章内容中的临时图片转换成正式的图片访问地址
			String articleContentTemp = article.getContent();
			
			//重新更新进去,articleContent.replaceAll后不会改变articleContent的值，在后面会继续进行临时图片的查询并替换
			article.setContent(article.getContent().replaceAll("/getTempImage\\?fileName=", "/getImage\\?id="+id+"&fileName="));
			articleMapper.updateArticle(article);
			
			//============================
			//查找文章内容中临时的图片，在文章保存（新增或修改）到数据库时，同时将临时目录下的图片迁移到以文章Id为目录的图片文件夹
			String tempImagePattern = "(getTempImage\\?fileName=)(\\w+-\\w+-\\w+-\\w+-\\w+\\.\\w+)(.*)";
			Pattern r = Pattern.compile(tempImagePattern);
			Matcher m = r.matcher(articleContentTemp);

			boolean imageDirCreted = false;
			while(m.find()){
				
				if(!imageDirCreted){
					Path newRealPath = Paths.get(imageAbsoluteFilePath+"/"+imageUploadPathPrex+"/"+id);
					//创建文件夹(不存在则创建)
					Files.createDirectories(newRealPath);		
					imageDirCreted = true;
				}
				
				String tempImageFileName = m.group(2);
			    try {
			    	Path fileRealPath = Paths.get(imageAbsoluteFilePath+"/" + imageUploadPathPrex + "/" + tempPrex + "/"+tempImageFileName);
			        Path newRealPathOfFile = Paths.get(imageAbsoluteFilePath+"/" + imageUploadPathPrex + "/"+id+"/"+tempImageFileName);
			        //覆盖式地复制文件
			        //Files.copy(fileRealPath, newRealPathOfFile, StandardCopyOption.REPLACE_EXISTING);
			        
			        //将临时图片移至正式目录（以文章id命名的目录）
			        logger.info("将临时图片移至正式目录,{},fileName:{}",id,tempImageFileName);
			        Files.move(fileRealPath, newRealPathOfFile);
			        
			        
			    } catch (IOException e) {
			        throw new RuntimeException("重命名文件失败");
			    }
			    
			}
			
			//最后，比对正式目录下的所有图片，将一些垃极图片从正式目录中也删除
			String tempImagePattern2 = "(getImage\\?id=\\d+&fileName=)(\\w+-\\w+-\\w+-\\w+-\\w+\\.\\w+)(.*)";
			Pattern r2 = Pattern.compile(tempImagePattern2);
			Matcher m2 = r2.matcher(article.getContent());
			HashSet<String> imageFileNameSet = new HashSet<String>();
			while(m2.find()){
				imageFileNameSet.add(m2.group(2));
			}
			File dir = new File(imageAbsoluteFilePath+"/"+imageUploadPathPrex+"/"+id);
			if(dir.exists()){
				String[] fileNames = dir.list();
				List<String> needDeleteFiles = new ArrayList<String>();
				for(String fileName : fileNames) {
					if(!imageFileNameSet.contains(fileName)){
						needDeleteFiles.add(fileName);
					}
				}
				for(String needDeleteFileName : needDeleteFiles){
					File file = new File(dir, needDeleteFileName);
					//从正式目录删除无效文件
			        logger.info("从正式目录删除无效文件,{},fileName:{}",id,needDeleteFileName);
					boolean success = file.delete();
					if(!success){
						throw new RuntimeException("删除无效文件失败："+ needDeleteFileName);
					}
				}				
			}
			
			
		} catch (Exception e) {
			logger.error("error",e);
			throw new RuntimeException(e.getMessage());
			
		} finally {
			
		}
		
		return article.getId();
	}


	@Override
	@Transactional
	public int updateArticle(Article article) {
		
		try {
			//取出自增id
			int id = article.getId();
			
			//将文章内容中的临时图片转换成正式的图片访问地址
			String articleContentTemp = article.getContent();
			
			//重新更新进去,articleContent.replaceAll后不会改变articleContent的值，在后面会继续进行临时图片的查询并替换
			article.setContent(article.getContent().replaceAll("/getTempImage\\?fileName=", "/getImage\\?id="+id+"&fileName="));
			articleMapper.updateArticle(article);
			
			//============================
			//查找文章内容中临时的图片，在文章保存（新增或修改）到数据库时，同时将临时目录下的图片迁移到以文章Id为目录的图片文件夹
			String tempImagePattern = "(getTempImage\\?fileName=)(\\w+-\\w+-\\w+-\\w+-\\w+\\.\\w+)(.*)";
			Pattern r = Pattern.compile(tempImagePattern);
			Matcher m = r.matcher(articleContentTemp);
			
			boolean imageDirCreted = false;
			while(m.find()){
				
				if(!imageDirCreted){
					Path newRealPath = Paths.get(imageAbsoluteFilePath+"/"+imageUploadPathPrex+"/"+id);
					//创建文件夹(不存在则创建)
					Files.createDirectories(newRealPath);	
					imageDirCreted = true;
				}
				
				String tempImageFileName = m.group(2);
			    try {
			    	Path fileRealPath = Paths.get(imageAbsoluteFilePath+"/" + imageUploadPathPrex + "/" + tempPrex + "/"+tempImageFileName);
			        Path newRealPathOfFile = Paths.get(imageAbsoluteFilePath+"/" + imageUploadPathPrex + "/"+id+"/"+tempImageFileName);
			        //覆盖式地复制文件
			        //Files.copy(fileRealPath, newRealPathOfFile, StandardCopyOption.REPLACE_EXISTING);
			        
			        //将临时图片移至正式目录（以文章id命名的目录）
			        logger.info("将临时图片移至正式目录,{},fileName:{}",id,tempImageFileName);
			        Files.move(fileRealPath, newRealPathOfFile);
			    } catch (IOException e) {
			        throw new RuntimeException("重命名文件失败");
			    }
			}
			
			//最后，比对正式目录下的所有图片，将一些垃极图片从正式目录中也删除
			String tempImagePattern2 = "(getImage\\?id=\\d+&fileName=)(\\w+-\\w+-\\w+-\\w+-\\w+\\.\\w+)(.*)";
			Pattern r2 = Pattern.compile(tempImagePattern2);
			Matcher m2 = r2.matcher(article.getContent());
			HashSet<String> imageFileNameSet = new HashSet<String>();
			while(m2.find()){
				imageFileNameSet.add(m2.group(2));
			}
			File dir = new File(imageAbsoluteFilePath+"/"+imageUploadPathPrex+"/"+id);
			if(dir.exists()){
				String[] fileNames = dir.list();
				List<String> needDeleteFiles = new ArrayList<String>();
				for(String fileName : fileNames) {
					if(!imageFileNameSet.contains(fileName)){
						needDeleteFiles.add(fileName);
					}
				}
				for(String needDeleteFileName : needDeleteFiles){
					File file = new File(dir, needDeleteFileName);
					//从正式目录删除无效文件
			        logger.info("从正式目录删除无效文件,{},fileName:{}",id,needDeleteFileName);
					boolean success = file.delete();
					if(!success){
						throw new RuntimeException("删除无效文件失败："+ needDeleteFileName);
					}
				}				
			}
			
		} catch (IOException e) {
			
			throw new RuntimeException(e.getMessage());
			
		} finally {
			
		}
		
		return 0;
	}

	@Override
	@Transactional
	public int deleteArticle(Article article) {
		
		articleMapper.deleteArticle(article);
		
        //将临时图片移至正式目录（以文章id命名的目录）
        logger.info("删除文章同时将ID的目录删除,{}",article.getId());
		
		//同时将ID的目录删除
	    try {
	    	File articleImagePath = new File(imageAbsoluteFilePath+"/"+imageUploadPathPrex+"/"+article.getId());
	    	if(articleImagePath.exists()){
	    		boolean success = deleteDir(articleImagePath);
		    	if(!success){
		    		throw new RuntimeException("删除文件目录失败"+ article.getId());
		    	}
	    	}
	    } catch (Exception e) {
	        throw new RuntimeException("删除文件目录失败"+ article.getId());
	        
	    }
		return 0;
	}
	
	//递归删除目录
	private boolean deleteDir(File dir){
		if(dir.isDirectory()){
			String[] children = dir.list();
			for(int i=0;i<children.length;i++){
				boolean success = deleteDir(new File(dir,children[i]));
				if(!success){
					return false;
				}
			}
		}
		return dir.delete();
	}

}
