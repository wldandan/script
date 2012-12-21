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


def main dest
  baseurl='http://www.ted.com'
  #doc=Nokogiri::HTML(open(baseurl))
  system "wget #{baseurl} -O index.html"
  content=open("index.html") { |f| f.lines.find { |line| line.include?("talksArray") } }
  content=content[content.index('{')+1,content.length]
  content.gsub!(';','')
  my_hash = JSON.parse("{#{content}")

  my_hash['talksArray'].each do |item|
    title=item['talkDate']+' ' + item['speaker']
    title.gsub!(' ','_')
    title.gsub!("'",'_')

    url=baseurl+item['talkLink']

    ted_doc=Nokogiri::HTML(open(url)) 
    ted_url=ted_doc.css('#no-flash-video-download')[0]['href']

    base_dir=Time.new.strftime('%Y-%m-%d')
    ted_dir="#{base_dir}/#{title}"

    download_mp4 url,ted_url,ted_dir
    puts "#{dest}/#{ted_dir}"
    parseContent "#{dest}/#{ted_dir}"
    return
  end
end

dest=ARGV[0]
dest=File.dirname(__FILE__) if "#{dest}".empty?
main dest
