import pymysql
import os

AWS_RDS_USER=os.environ.get("AWS_RDS_USER")
AWS_RDS_PORT=os.environ.get("AWS_RDS_PORT")
AWS_RDS_PASSWD=os.environ.get("AWS_RDS_PASSWD")
AWS_RDS_HOST=os.environ.get("AWS_RDS_HOST")
AWS_RDS_DB=os.environ.get("AWS_RDS_DB")

conn = pymysql.connect(
    user=AWS_RDS_USER,
    passwd=AWS_RDS_PASSWD,
    host=AWS_RDS_HOST,
    port=int(AWS_RDS_PORT),
    db=AWS_RDS_DB,
    charset='utf8'
)
cursor = conn.cursor(pymysql.cursors.DictCursor)

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
        where p1.category =%s and p1.modified_date > curdate()
        group by p1.product_id having count > 5 order by null) temp
        on duplicate key update min_price = temp.min_price, avg_price =  temp.avg_price , today_price =temp.today_price, count =  temp.count, modified_date = temp.modified_date"""
    cursor.execute(today_minimum_price_product_batch, i)
    print("오늘 최저가 상품 배치 : " + i + " 완료")

conn.commit()
print("배치 완료")