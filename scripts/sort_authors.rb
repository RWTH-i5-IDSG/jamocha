#!/usr/bin/env ruby

Dir["**/*.java"].each do |filename|
	changed = false
	lines = IO.readlines(filename);
	blockstart = -1
	for i in 0 .. lines.count do
		if (blockstart == -1) then
			if (/@author/.match(lines[i])) then
				blockstart = i
			end
		elsif not (/@author/.match(lines[i])) then
			if (i - blockstart > 1) then
				ll = lines[blockstart..(i-1)]
				ll.sort_by! do |line|
					s = line.split(" ")
					j = 0
					while s[j][0] != "<" do
						j += 1
					end
					"#{s[(j-1)]}, #{s[(j-2)]}"
				end
				lines[blockstart..(i-1)] = ll
				changed = true
			end
			blockstart = -1
		end
	end
	if changed then
		File.open(filename, "w") do |file|
			file.write(lines.join(""))
		end
	end
end
