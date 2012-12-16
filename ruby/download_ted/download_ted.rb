require 'nokogiri'  
require 'open-uri'  
require 'json'


def download_mp4 title,url,ted_url
  base_dir=Time.new.strftime('%Y-%m-%d')
  ted_dir="#{base_dir}/'#{title}'"
  system "mkdir -p #{ted_dir}"
  puts "saving #{ted_url} to #{ted_dir}"
  system "wget #{url} -O #{ted_dir}/index.html"
  system "wget #{ted_url} -P #{ted_dir}"
  parseContent ted_dir
end

def parseContent base_dir
  result=''
  content=open("#{base_dir}/index.html") { |f| f.lines.each { |line| result<<line if line.include?('class="transcriptLink"') } }
  content_doc=Nokogiri::HTML(content) 
  content_doc.css('.transcriptLink').each do |item|
    `echo "#{item.inner_text}" >> "#{base_dir}/content.txt"`
  end 
end


def main
  baseurl='http://www.ted.com'
  doc=Nokogiri::HTML(open(baseurl))
  system "wget #{baseurl} -O index.html"
  content=open("index.html") { |f| f.lines.find { |line| line.include?("talksArray") } }
  content.gsub!(';','')
  my_hash = JSON.parse("{#{content}")

  my_hash['talksArray'].each do |item|
    title=item['talkDate']+' ' + item['speaker']
    title.gsub!(' ','_')

    url=baseurl+item['talkLink']

    ted_doc=Nokogiri::HTML(open(url)) 
    ted_url=ted_doc.css('#no-flash-video-download')[0]['href']
    download_mp4 title,url,ted_url
  end
end

main
