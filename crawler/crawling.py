import pymysql
import os
from musinsa_crawler import crawling
import sys

AWS_RDS_USER=os.environ.get("AWS_RDS_USER")
AWS_RDS_PORT=os.environ.get("AWS_RDS_PORT")
AWS_RDS_PASSWD=os.environ.get("AWS_RDS_PASSWD")
AWS_RDS_HOST=os.environ.get("AWS_RDS_HOST")
AWS_RDS_DB=os.environ.get("AWS_RDS_DB")

price_sql = "INSERT INTO price(product_id, rank, price, del_price, rating, rating_count, created_date, coupon, real_price) values(%s,%s, %s, %s, %s, %s, %s, %s, %s)"
product_sql = """INSERT INTO product(product_id, img, product_name, product_url, brand, brand_url, modified_date, category, rank, real_price)
                values(%s,%s, %s, %s, %s, %s, %s, %s, %s, %s)
                ON DUPLICATE KEY UPDATE
                product_id = values(product_id), img = values(img), product_name = values(product_name), product_url = values(product_url), brand = values(brand),
                brand_url = values(brand_url), modified_date = values(modified_date) , category = values(category), rank = values(rank), real_price = values(real_price)"""
pages = [1, 11, 21, 31, 41, 51, 61, 71, 81, 91]
price_list=[]
product_list=[]

for page in pages:
    data = crawling(page, page+9)
    for d in data:
        price = (str(d["item_id"]), str(d["rank"]), str(d["price"]), str(d["del_price"]), d["rating"], str(d["rating_count"]), str(d["time"]), str(d["coupon"]), str(int(d["price"]) + int(d["coupon"])))
        product = (str(d["item_id"]), str(d["img"]), str(d["product_name"]), str(d["product_url"]), d["brand"], str(d["brand_url"]), str(d["time"]), d["category"],str(d["rank"]), str(int(d["price"]) + int(d["coupon"])))
        price_list.append(price)
        product_list.append(product)
    print("page done!")

conn = pymysql.connect(
    user=AWS_RDS_USER,
    passwd=AWS_RDS_PASSWD,
    host=AWS_RDS_HOST,
    port=int(AWS_RDS_PORT),
    db=AWS_RDS_DB,
    charset='utf8'
    )

cursor = conn.cursor(pymysql.cursors.DictCursor)

cursor.executemany(price_sql, price_list)
cursor.executemany(product_sql, product_list)


print("오늘 할인 상품 배치 시작")
today_discount_batch = """insert into today_discount_product(product_id, discount, percent, modified_date, created_date)
    select p1.product_id, p2.real_price - p1.real_price, (p2.real_price - p1.real_price)*100/p2.real_price, p1.modified_date, p1.modified_date from  product p1
    inner join  price p2 on p1.product_id = p2.product_id
    where  p1.modified_date > curdate() and p2.created_date > curdate() - interval 1 day and p1.real_price < p2.real_price
    on duplicate key  update  discount = p2.real_price - p1.real_price, percent = (p2.real_price - p1.real_price)*100/p2.real_price, modified_date = p1.modified_date"""
cursor.execute(today_discount_batch)
print("오늘 할인 상품 배치 완료")

print("오늘 최저가 상품 배치 시작")
category = ['001', '002', '003', '004', '018', '005', '007', '020', '022', '008']
for i in category:
    print("오늘 최저가 상품 배치 : " + i + " 시작")
    today_minimum_price_product_batch = """insert into today_minimum_price_product(product_id, min_price, avg_price, today_price, count, created_date, modified_date)
        select temp.product_id, temp.min_price , temp.avg_price , temp.today_price, temp.count, temp.created_date, temp.modified_date from
        (select p1.product_id, min(p2.real_price) as min_price , avg(p2.real_price) as avg_price, p1.real_price as today_price, count(*) as count, p1.modified_date as created_date, p1.modified_date as modified_date
        from product p1
        inner join price p2 on p1.product_id = p2.product_id
        where p1.category =%s and p1.modified_date > curdate() and p2.created_date > curdate() - interval 30 day
        group by p1.product_id having count > 5 order by null) temp
        on duplicate key update min_price = temp.min_price, avg_price =  temp.avg_price , today_price =temp.today_price, count =  temp.count, modified_date = temp.modified_date"""
    cursor.execute(today_minimum_price_product_batch, i)
    print("오늘 최저가 상품 배치 : " + i + " 완료")

conn.commit()
print("배치 완료")

conn.commit()
