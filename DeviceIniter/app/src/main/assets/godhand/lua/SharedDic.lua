SharedDic = {SharedDicData = {}}

function SharedDic.get(key)
	return SharedDic.SharedDicData[key]
end

function SharedDic.set(key, value)
	SharedDic.SharedDicData[key] = value
end