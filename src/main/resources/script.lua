local discoverWord = ARGV[3]
local completeWord = ARGV[2]

for i = 1, #completeWord do
    local ch = completeWord:sub(i,i)
    if ch == ARGV[1]
	then
        discoverWord=discoverWord:sub(1, i-1)..ch..discoverWord:sub(i+1)
    end
end

return discoverWord