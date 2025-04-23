# flask_server.py
from flask import Flask, request, jsonify
import face_recognition
import numpy as np
import cv2
import base64

app = Flask(__name__)

# Charger les visages connus
known_image = face_recognition.load_image_file("known_person.jpg")
known_encoding = face_recognition.face_encodings(known_image)[0]

@app.route("/recognize", methods=["POST"])
def recognize():
    data = request.get_json()
    img_data = base64.b64decode(data["image"])
    nparr = np.frombuffer(img_data, np.uint8)
    img = cv2.imdecode(nparr, cv2.IMREAD_COLOR)

    encodings = face_recognition.face_encodings(img)
    if encodings:
        match = face_recognition.compare_faces([known_encoding], encodings[0])
        return jsonify({"match": match[0]})
    return jsonify({"match": False})

if __name__ == "__main__":
    app.run(port=5000)
