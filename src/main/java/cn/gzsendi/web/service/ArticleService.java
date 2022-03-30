package cn.gzsendi.web.service;

import java.util.List;

import cn.gzsendi.web.model.Article;

public interface ArticleService {
	//查看所有文章列表
	public List<Article> getArticleList();
    //添加文章
    public int addArticle(Article article);
    //更新文章
    public int updateArticle(Article article);
    //删除文章
    public int deleteArticle(Article article);
    //根据文章id查询文章
    public Article getArticleById(Integer id);
}
