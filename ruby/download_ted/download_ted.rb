require 'nokogiri'  
require 'open-uri'  
require 'json'

def download_mp4 url,ted_url,ted_dir
  system "mkdir -p #{ted_dir}"
  puts "saving #{ted_url} to #{ted_dir}"
  system "wget #{url} -O #{ted_dir}/index.html"
  system "wget #{ted_url} -P #{ted_dir}"
end

def parseContent base_dir
  result=''
  contents='';
  File.open("#{base_dir}/index.html") { |f| f.lines.each { |line| result<<line if line.include?('class="transcriptLink"') } }
  content_doc=Nokogiri::HTML(result)
  content_doc.css('.transcriptLink').each do |item|
    item.inner_text.strip!
    contents << "#{item.inner_text}\n"
  end

  File.open("#{base_dir}/content.txt", 'w') { |file| file.write(contents) }
end

def process_mp4 (item, base_dir, base_url, dest)
    title=item['talkDate']+' ' + item['speaker']
    title.gsub!(' ','_')
    title.gsub!("'",'_')
    url=base_url+item['talkLink']
    ted_doc=Nokogiri::HTML(open(url)) 
    ted_url=ted_doc.css('#no-flash-video-download')[0]['href']
    ted_url.gsub!("?apikey=TEDDOWNLOAD",'')
    puts "Downloading #{ted_url}"
    ted_dir="#{base_dir}/#{title}"

    download_mp4 url,ted_url,ted_dir
    puts "#{dest}/#{ted_dir}"
    parseContent "#{dest}/#{ted_dir}"
end  

def main dest
  #doc=Nokogiri::HTML(open(baseurl))
  base_url='http://www.ted.com'
  #system "wget #{base_url} -O index.html"
  content=open("#{base_url}") { |f| f.lines.find { |line| line.include?("talksArray") } }
  content=content[content.index('{')+1,content.length]
  content.gsub!(';','')
  my_hash = JSON.parse("{#{content}")
  base_dir=Time.new.strftime('%Y-%m-%d')
  threads=[]
  my_hash['talksArray'].each do |item|
    puts item['speaker'] 
    threads<<Thread.new{ 
      process_mp4(item,base_dir,base_url,dest) 
   }
  end
  threads.each {|thr| thr.join}
end

dest=ARGV[0]
dest=File.dirname(__FILE__) if "#{dest}".empty?
main dest
