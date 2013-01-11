require 'nokogiri'
require 'open-uri'
require 'json'


class TedCrawler
  TED_HOME = "http://www.ted.com"

  attr_accessor :base_dir
  def initialize(base_dir='/tmp')
    @base_dir = base_dir
  end

  def dir_name
    Time.new.strftime('%Y-%m-%d')
  end

  def download_mp4 (talk_index_url, talk_mp4_url, relative_save_dir)
    system "wget #{talk_index_url} -O #{@base_dir}/#{relative_save_dir}/index.html"
    system "wget #{talk_mp4_url} -P #{@base_dir}/#{relative_save_dir}"
  end

  def parse_talk_text dir
    result=''
    File.open("#{dir}/index.html") { |f| f.lines.each { |line| result<<line if line.include?('class="transcriptLink"') } }
    contents='';
    Nokogiri::HTML(result).css('.transcriptLink').each do |item|
      item.inner_text.strip!
      contents << "#{item.inner_text}\n"
    end

    File.open("#{dir}/content.txt", 'w') { |file| file.write(contents) }
  end

  def process_mp4 (item)
    talk_index_url = TED_HOME + item['talkLink']
    talk_mp4_url=Nokogiri::HTML(open(talk_index_url)).css('#no-flash-video-download')[0]['href']
    title="#{item['talkDate']}-#{item['speaker']}".gsub(' ','_').gsub("'",'_')
    relative_save_dir="#{dir_name}/#{title}"

    system "mkdir -p #{relative_save_dir}"
    download_mp4 talk_index_url,talk_mp4_url,relative_save_dir
    parse_talk_text relative_save_dir
  end

  def talks
    content=open(TED_HOME) { |f| f.lines.find { |line| line.include?("talksArray") } }
    content=content[content.index('{')+1,content.length].gsub!(';','')
    JSON.parse("{#{content}")['talksArray']
  end

  def main
    threads=[]
    talks.each do |talk|
      threads<<Thread.new{ process_mp4(talk)}
    end
    threads.each {|t| t.join}
  end

end

dest=ARGV[0]
dest=File.dirname(__FILE__) if "#{dest}".empty?
TedCrawler.new(dest).main
