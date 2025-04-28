from flask import Flask, request, jsonify
import face_recognition
import numpy as np
import cv2
import os
#import jwt
import datetime


app = Flask(__name__)

image_directory = "C:/xampp/htdocs/img"
SECRET_KEY = "my_super_secret_key123"

def load_face_encodings():
    encodings = []
    images = []
    print(f"Recherche d'images dans: {image_directory}")
    try:
        file_list = os.listdir(image_directory)
        print(f"Fichiers trouvés: {file_list}")
        for filename in file_list:
            if filename.endswith(".jpg") or filename.endswith(".png"):
                print(f"Traitement de l'image: {filename}")
                image_path = os.path.join(image_directory, filename)
                image = face_recognition.load_image_file(image_path)
                print(f"Image chargée, recherche de visages...")
                encoding = face_recognition.face_encodings(image)
                print(f"Visages trouvés: {len(encoding)}")
                if encoding:
                    print("Visage détecté et encodé avec succès")
                    encodings.append(encoding[0])
                    images.append(image_path)
    except Exception as e:
        print(f"Erreur lors du chargement des images: {e}")

    print(f"Total des visages encodés: {len(encodings)}")
    return encodings, images


known_face_encodings, known_face_images = load_face_encodings()

@app.route('/compare_face', methods=['POST'])
def compare_face():
    file = request.files['image']
    img = face_recognition.load_image_file(file)


    captured_encoding = face_recognition.face_encodings(img)
    print("Nombre de visages détectés dans l'image reçue :", len(face_recognition.face_locations(img)))

    # Si aucun visage n’est détecté, on retourne un message directement
    if len(face_recognition.face_locations(img)) == 0:
        return jsonify({'message': 'No face detected in the uploaded image'}), 200


    if captured_encoding:
        captured_encoding = captured_encoding[0]

        for encoding, image_path in zip(known_face_encodings, known_face_images):
            matches = face_recognition.compare_faces([encoding], captured_encoding,tolerance=0.5)
            if matches[0]:
                user_filename = os.path.basename(image_path)
                payload = {
                    'user_filename': user_filename,
                    'exp': datetime.datetime.utcnow() + datetime.timedelta(hours=1)
                }
                #token = jwt.encode(payload, SECRET_KEY, algorithm='HS256')
                return jsonify({"message": f"Face matched with image: {image_path}"}), 200


    return jsonify({"message": "No match found!"}), 200

if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=8889)
