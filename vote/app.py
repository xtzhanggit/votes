from flask import Flask, request, make_response, render_template, g
from redis import Redis
import socket
import os
import random
import json

option_a = os.getenv('OPTION_A', "Test_a")
option_b = os.getenv('OPTION_B', "Test_b")
hostname = socket.gethostname()

app = Flask(__name__)

def get_redis():
    if not hasattr(g, 'redis'):
        g.redis = Redis(host = 'redis',db = 0,socket_timeout = 5)
    return g.redis
        
@app.route("/", methods = ['GET','POST'])
def vote():
    voter_id = request.cookies.get('voter_id')
    if not voter_id:
        voter_id = hex(random.getrandbits(64))[2:-1]
    print(voter_id)

    vote = None
    
    if request.method == 'POST':
        redis = get_redis()
        vote = request.form['vote']
        data = json.dumps({'voter_id':voter_id,'vote':vote})
        redis.rpush('votes',data)
        
    resp = make_response(
        render_template(
            'index.html',
            option_a = option_a,
            option_b = option_b,
            hostname = hostname,
            vote = vote
        )
    )
    resp.set_cookie('voter_id',voter_id)
    return resp

if __name__ == "__main__":
    app.run(host = '0.0.0.0', port = 80, debug = True, threaded = True)

