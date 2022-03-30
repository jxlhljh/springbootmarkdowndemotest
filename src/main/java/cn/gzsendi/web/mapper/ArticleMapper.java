package cn.gzsendi.web.mapper;

import java.util.List;

import cn.gzsendi.web.model.Article;

public interface ArticleMapper {
	
	//查询所有文章
	List<Article> getArticleList();
	
	//根据ID获取文章
	Article getArticleById(Integer id);
	
    //添加文章
    int addArticle(Article article);
    
    //更新文章
    int updateArticle(Article article);
   
    //删除文章
    int deleteArticle(Article article);
	
}
