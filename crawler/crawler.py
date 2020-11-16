from bs4 import BeautifulSoup
import time
import requests
from datetime import datetime
from pytz import timezone

kst = datetime.now(timezone('Asia/Seoul')).strftime('%Y-%m-%d')

site = "https://okky.kr/articles/tech-qna?offset=0&max=20&sort=id&order=desc"
domain = 'https://okky.kr'

def get_url_list():
    page_list = []
    idx = 0
    while True:
        count = 0
        source = requests.get("https://okky.kr/articles/tech-qna?offset="+ str(idx * 20) + "&max=20&sort=id&order=desc").text
        soup = BeautifulSoup(source, "html.parser")
        posts = soup.findAll("li", "list-group-item list-group-item-question list-group-no-note clearfix")
        posts2 = soup.findAll("li", "list-group-item list-group-item-question list-group-has-note clearfix")
        posts.extend(posts2)
        for post in posts:
            try:
                post_time = datetime.strptime(post.find("span", "timeago").get("title"), '%Y-%m-%dT%H:%M:%S')
            except:
                post_time = datetime.strptime(post.find("span", "timeago").get("title"), '%Y-%m-%dT%H:%M')
            if post_time.strftime('%Y-%m-%d') == kst:
                page_list.append('https://okky.kr' + post.select("h5 > a ")[0].get("href"))
                count+=1
        idx+=1
        if count == 0: break
    return page_list

def crawling():
    data = []
    page_list = get_url_list()
    for url in page_list:
        resource = requests.get(url).text
        soup = BeautifulSoup(resource, "html.parser")
        question = {}
        question["title"] = soup.find('h2', 'panel-title')
        question["content"] = soup.find('article', 'content-text')
        try:
            question["user_id"] = soup.findAll("div","panel panel-default clearfix fa-")[0].find("a", "nickname").get("title")
        except:
            question["user_id"] = "guest"
        try:
            question["create_date"] = datetime.strptime(soup.findAll("div","panel panel-default clearfix fa-")[0].find("span", "timeago").get("title"), '%Y-%m-%dT%H:%M:%S')
        except:
            question["create_date"] = datetime.strptime(soup.findAll("div","panel panel-default clearfix fa-")[0].find("span", "timeago").get("title"), '%Y-%m-%dT%H:%M')

        answers = []

        answer_list = soup.findAll('div', "content-body panel-body pull-left")
        for answer in answer_list:
            answer_dict = {}
            answer_dict["title"] = soup.find('h2', 'panel-title')
            answer_dict["content"] =  answer.find("article")
            try:
                answer_dict["user_id"] = answer.find("a", "nickname").get("title")
            except:
                answer_dict["user_id"] = "guest"
            try:
                answer_dict["create_date"] = datetime.strptime(answer.find("span", "timeago").get("title"), '%Y-%m-%dT%H:%M:%S')
            except:
                answer_dict["create_date"] = datetime.strptime(answer.find("span", "timeago").get("title"), '%Y-%m-%dT%H:%M')
            answers.append(answer_dict)
        data.append([question, answers])
    return data