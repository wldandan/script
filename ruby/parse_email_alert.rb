require  'mysql'
begin
  #puts "connect to mysql database"
  dbh = Mysql.real_connect("localhost", "root", "","rea", 3306)   #连接数据库本机：用户名：root 密码：root 数据库：chj 端口：3306
  #puts "connect success"
  #puts "==============="
  #dbh.query("drop table if exists member")
  #dbh.query("create table member(memberId int,email varchar(25),aliww varchar(30))")
  #dbh.query("insert into member values(1,'abc@163.com,'zhangsan')")
  #printf "%d rows were inserted/n",dbh.affected_rows
  res = dbh.query("select v.`visitor_login_id`, s.`name`, s.`ntf_frequency`
      from visitor v, saved_search s
      where s.`visitor_uid` = v.`visitor_uid`
         and s.`search_type`='saved' and s.`ntf_frequency` is not NULL and v.`visitor_login_id` is not null")
  data=[]
  while row = res.fetch_row do
    data << row.map{|value| value.gsub(',','+').gsub(' ', '')}.join(",\t")
  end


  puts Time.now.strftime("%Y-%m-%d-%H-%M-%S")
  puts data

  file_name = "/jobs/email-alert/" + Time.now.strftime("%Y-%m-%d-%H-%M-%S") + ".csv_ruby"
  File.open(file_name, 'w') do |f|
      f.puts data
  end
  #  # use "\n" for two lines of text
  puts "saving file #{file_name}"
  #end
rescue Mysql::Error=>e
  #puts "Error code:#{e.errno}"
  #puts "Error message:#{e.error}"
  #puts "Error SQLSTATE:#{e.sqlstate}" if e.respond_to?("sqlstate")
ensure
  dbh.close if dbh
  #puts "close the connection"
end