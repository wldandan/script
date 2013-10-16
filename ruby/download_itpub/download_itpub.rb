require 'nokogiri'  
require 'open-uri'  
require 'json'
require 'mechanize'
require 'curb'
require 'httpclient'


def is_in_download_list?(title)
  unless title.empty?
      ext = title[title.rindex('.')..-1]
      ['.pdf','.rar','.zip','.tar','.epub'].include?(ext)
  end
end

login_url = 'http://www.itpub.net/member.php?mod=logging&action=login&loginsubmit=yes&infloat=yes&lssubmit=yes'

clnt = HTTPClient.new
#clnt.set_cookie_store('cookie.dat')
body = { 'username' => 'wldandan', 'password' => '1981119', 'cookietime' => '259200' }

res = clnt.post(login_url, body).content
res = clnt.get(ARGV[0]).content

doc=Nokogiri::HTML(res)

parse_rule = "ignore_js_op a"
doc.css(parse_rule).each do |item|
  relative_url = item['href']
  title=item.inner_text
  if is_in_download_list?(title)
    url="http://www.itpub.net/#{relative_url}"
    url.gsub!('attachment.php?','forum.php?mod=attachment&')
    system "curl --cookie /tmp/cookie -o '#{title}' '#{url}'"
  end
end
