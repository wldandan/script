require 'nokogiri'  
require 'open-uri'  
require 'json'

baseurl=ARGV[0]
puts baseurl
doc=Nokogiri::HTML(open(baseurl))

links = doc.css("ignore_js_op span a").each do |item|
  relative_url = item['href']
  title=item.inner_text
  url="http://www.itpub.net/#{relative_url}"
  url.gsub!('attachment.php?','forum.php?mod=attachment&')
  #system "curl -o '#{title}' '#{url}'"
  system "curl -o \"#{title}\" '#{url}'"
end  
