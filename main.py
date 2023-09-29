'''
main.py
'''
from fastapi import FastAPI
from file.controller import fileController
from folder.controller import folderController
from database import engine, Base
from config import Config
import uvicorn

app = FastAPI()
config = Config()

Base.metadata.create_all(bind=engine)
app.include_router(fileController)
app.include_router(folderController)

if __name__ == "__main__":
    uvicorn.run("main:app", host=config.HOST, port=config.PORT, reload=True)
