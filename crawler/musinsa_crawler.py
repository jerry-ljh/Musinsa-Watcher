from bs4 import BeautifulSoup
import requests
import urllib.request
from datetime import datetime
from pytz import timezone
import re

def crawling(start, end):
    data = []
    category = ['001', '002', '003', '004', '018', '005', '007', '020', '022', '008']
    kst = datetime.now(timezone('Asia/Seoul')).strftime('%Y-%m-%dT%H:%M')
    for page in range(start,end+1):
        for c in category:
            try :
                site = "https://search.musinsa.com/ranking/best?period=day&mainCategory="+ c +"&subCategory=&price=&newProduct=false&discount=false&soldOut=false&page="+str(page)+"&viewType=small&device=&priceMin=&priceMax="
                source = requests.get(site).text
                soup = BeautifulSoup(source, "html.parser")
                total_page_num = int(re.sub('<.+?>', '', str(soup.select('.totalPagingNum')[0])))
                if total_page_num < page:
                    continue
                items = soup.select("#goodsRankList")[0].findAll("li", "li_box")
            except :
                continue
            dict_list = []
            for item in items:
                item_dict = {}
                item_dict["item_id"] = int(item.get("data-goods-no"))
                item_dict["rank"] = int(re.sub('<.+?>', '', str(item.find("p", "n-label label-default txt_num_rank"))).strip().split("위")[0])
                item_dict["img"] = item.find("img").get("data-original")
                item_dict["brand_url"] = item.select("p.item_title > a")[0].get("href")
                item_dict["brand"]= re.sub('<.+?>', '', str(item.select("p.item_title > a")[0])).strip()
                item_dict["product_name"] = item.select("p.list_info > a")[0].get('title').strip()
                coupon = item.find("p", "mu-icon mu-icon-coupon")
                item_dict["coupon"] = coupon
                if coupon :
                    item_dict["coupon"] = re.sub('<.+?>', '', str(coupon.find("span", "txt_discount_price"))).replace("원", "").replace(",", "")
                else :
                    item_dict["coupon"] = 0
                try:
                    item_dict["rating_count"] = int(re.sub('<.+?>', '', str(item.find("span", "count"))).replace(",", ""))
                    item_dict["rating"] = float(item.select("span.bar")[0].get("style").strip().split(" ")[1].replace("%", ""))
                except:
                    item_dict["rating_count"] = 0
                    item_dict["rating"] = 0
                item_dict["price"] = int(re.sub('<.+?>', '', str(item.select("span.txt_price_member")[0])).strip().split("원")[0].replace(",", ""))
                item_dict["product_url"] = items[0].select("p.list_info > a")[0].get("href")
                try:
                    item_dict["del_price"] = int(re.sub('<.+?>', '', str(item.select("p.price > del")[0])).strip().split("원")[0].replace(",", ""))
                except:
                    item_dict["del_price"] = 0
                item_dict["category"] = c
                item_dict["time"] = kst
                dict_list.append(item_dict)
            data.extend(dict_list)
        print("page : " + str(page) + " done")
    return data