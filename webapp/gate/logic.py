import json
import html2text
import requests
from subprocess import Popen, PIPE, STDOUT


def text_from_url(url):
    '''Returns string of text from the given URL'''
    try:
        response = requests.get(url)
        if response.status_code != 200:
            return None
        h = html2text.HTML2Text()
        h.ignore_links = True
        h.ignore_images = True
        h.ignore_emphasis = True
        return h.handle(response.text)
        
    except requests.ConnectionError:
        return None


def gate(text):
    '''Runs the given text trough the Java app'''
    jarfile = '../GATE-counter/dist/GATE-counter.jar'
    p = Popen(['java', '-jar', jarfile], stdout=PIPE, stdin=PIPE, stderr=PIPE)
    return p.communicate(input=text.encode('UTF-8'))[0]
    


def tokenize(url):
    '''Tokenizes the given URL'''
    text = text_from_url(url)
    major = {}
    minor = {}
    for line in gate(text).split('\n'):
        if not line:
            continue
        key, value = line.strip().split()
        if ':' not in key:
            major[key] = value
        else:
            primary, secondary = key.split(':')
            if not primary in minor:
                minor[primary] = {}
            minor[primary][secondary] = value
    return major, minor


def flot_data(data):
    '''Return JSON of data how flot expects it'''
    tojson = []
    for key, value in data.items():
        if key == '_total_':
            continue
        tojson.append({'label': key, 'data': value})
    return json.dumps(tojson)
