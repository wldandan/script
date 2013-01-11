directory='/Users/twer/Work/rea/consumer-search'
Dir.foreach(directory) do |item|
  u_dir = directory + File::Separator + item unless item.include?('.')
  if u_dir && File.stat(u_dir).directory?
    system %{set -x; cd #{u_dir}}
    system %{git pull --rebase}
  end
end
