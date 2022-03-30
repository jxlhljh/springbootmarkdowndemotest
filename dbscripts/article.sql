/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 50731
 Source Host           : localhost:3306
 Source Schema         : test

 Target Server Type    : MySQL
 Target Server Version : 50731
 File Encoding         : 65001

 Date: 29/03/2022 16:27:38
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for article
-- ----------------------------
DROP TABLE IF EXISTS `article`;
CREATE TABLE `article`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `title` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文章名称',
  `content` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '文章md的内容',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of article
-- ----------------------------
INSERT INTO `article` VALUES (5, 'Java使用thumbnailator进行图片压缩缩放裁剪水印旋转处理', '[TOC]\n\n# 一、thumbnailator介绍\n纯Java开源类库Thumbnailator，由Google提供。支持的操作有：图片压缩、图片缩放，图片裁剪，水印，旋转等\n\n# 二、使用步骤\n\n## 1.maven的pom.xml引入如下\n```c\n<dependency>\n   <groupId>net.coobird</groupId>\n   <artifactId>thumbnailator</artifactId>\n   <version>0.4.8</version>\n</dependency>\n```\n## 2.测试代码\n```\n//进行图片压缩，图片尺寸不变，压缩图片文件大小outputQuality实现,参数1为最高质量\n//scale 图片的压缩比例 值在0-1之间，1f就是原图，0.5就是原图的一半大小\n//outputQuality 图片压缩的质量 值在0-1 之间，越接近1质量越好，越接近0质量越差\n```\n```c\nThumbnails.of(\"d:/temp/source.jpg\").scale(1f).outputQuality(0.25f).outputFormat(\"jpg\").toFile(\"d:/temp/target.jpg\");\n```\n`图片尺寸按比例放大或缩小，图片不压缩,scale(0.5f)表示缩小到一半`\n```c\nThumbnails.of(\"d:/temp/source.jpg\").scale(0.1f).outputQuality(1.0f).outputFormat(\"jpg\").toFile(\"d:/temp/target.jpg\");\n```\n\n```\n//指定大小进行缩放\n/*\n * size(width,height) 若图片横比200小，高比300小，不变\n * 若图片横比200小，高比300大，高缩小到300，图片比例不变 若图片横比200大，高比300小，横缩小到200，图片比例不变\n * 若图片横比200大，高比300大，图片按比例缩小，横为200或高为300\n */\n```\n```c\nThumbnails.of(\"d:/temp/source.jpg\").size(200, 300).outputQuality(1.0f).outputFormat(\"jpg\").toFile(\"d:/temp/target.jpg\");\n```\n`不按照比例，指定大小进行缩放，keepAspectRatio(false) 默认是按照比例缩放的`\n```c\nThumbnails.of(\"d:/temp/source.jpg\").size(200, 300).keepAspectRatio(false).toFile(\"d:/temp/target.jpg\");\n```\n`旋转,rotate(角度),正数：顺时针 负数：逆时针\n`\n```c\nThumbnails.of(\"d:/temp/source.jpg\").size(200, 300).rotate(90).toFile(\"d:/temp/target.jpg\");\n```\n`水印.watermark(位置，水印图，透明度)`\n```c\nThumbnails.of(\"d:/temp/source.jpg\").size(300, 200).watermark(Positions.CENTER, ImageIO.read(new File(\"D:/temp/watermark.png\")), 0.5f).toFile(\"d:/temp/target.jpg\");\n```\n`裁剪`\n```c\nThumbnails.of(\"d:/temp/source.jpg\").sourceRegion(Positions.TOP_LEFT, 500, 500).size(400, 400).toFile(\"d:/temp/target.jpg\");\n```\n`转换图片格式`\n```c\nThumbnails.of(\"d:/temp/source.jpg\").scale(1f).outputFormat(\"png\").toFile(\"d:/temp/target.png\");\n```\n`输出成文件流OutputStream`\n```c\n		//这里的OutputStream可以换成springboot的response的输出流，这样就可以直接输出到浏览器了\n		OutputStream os = new FileOutputStream(\"d:/temp/target.jpg\");\n		//同样of方法后面可以接Inputstream输入流，不一定需要文件\n		Thumbnails.of(\"d:/temp/source.jpg\").scale(1f).outputFormat(\"jpg\").toOutputStream(os);\n```');
INSERT INTO `article` VALUES (6, 'demotest', '[TOC]\n\n=====================标题==================\n# 一级标题\n## 二级标题\n### 三级标题\n#### 四级标题\n##### 五级标题\n###### 六级标题\n\n=====================文本样式==================\n*强调文本* _强调文本_\n**加粗文本** __加粗文本__\n==标记文本==\n~~删除文本~~\n> 引用文本\n\nH~2~O is是液体。\n\n`H~2~O is是液体。`\n\n`2^10^ 运算结果是 1024。`\n\n2^10^ 运算结果是 1024。\n=====================列表==================\n- 项目\n  * 项目\n    + 项目\n	\n\n1. 项目1\n2. 项目2\n3. 项目3\n\n- [ ] 计划任务\n- [x] 完成任务\n\n=====================链接==================\n链接: [link](https://www.csdn.net/).\n\n图片: ![Alt](https://imgconvert.csdnimg.cn/aHR0cHM6Ly9hdmF0YXIuY3Nkbi5uZXQvNy83L0IvMV9yYWxmX2h4MTYzY29tLmpwZw)\n\n带尺寸的图片: ![Alt](https://imgconvert.csdnimg.cn/aHR0cHM6Ly9hdmF0YXIuY3Nkbi5uZXQvNy83L0IvMV9yYWxmX2h4MTYzY29tLmpwZw =30x30)\n\n=====================目录==================\n[TOC]\n# 一级目录\n## 二级目录\n### 三级目录\n=====================代码片==================\n下面展示一些 `内联代码片`。\n\n```\n// A code block\nvar foo = \'bar\';\n```\n\n```javascript\n// An highlighted block\nvar foo = \'bar\';\n```\n=====================表格==================\n项目     | Value\n-------- | -----\n电脑  | $1600\n手机  | $12\n导管  | $1\n\n| Column 1 | Column 2      |\n|:--------:| -------------:|\n| centered 文本居中 | right-aligned 文本居右 |\n\n');

SET FOREIGN_KEY_CHECKS = 1;
