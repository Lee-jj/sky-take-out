import requests
import json

url = 'http://localhost:8080/user/user/login/v1'
fw = open('tools/token.txt','w')
for i in range(5000):
    phone = '1234567' + str(i).zfill(4)
    data = {'password': '123456','phone':phone}
    req = requests.post(url, json=data)
    reqdict = json.loads(req.text)
    print(i)
    fw.write(reqdict['data']['token'] + '\n')
fw.close()
print('done...')

