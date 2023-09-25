from typing import Annotated
from sqlalchemy.orm import Session

from fastapi import APIRouter, Depends, HTTPException
from model.models import Folders
from utils.utils import get_db 
from folder.service import (
    get_folder_by_name,
    create_new_folder,
    update_folder_data,
    delete_folder_data,
    move_folder_data,
    get_folder_data
)

folderController = APIRouter(prefix="/folder")

db_dependency = Annotated[Session, Depends(get_db)]

""" 
POST : Create folder for specific user
"""
@folderController.post("/{username}/create")
async def create_folder(
        db: db_dependency,
        folder_name: str,
        username: str,
        parent_name: str = "root",
    ):
    existing_folder = get_folder_by_name(db, username, folder_name)
    if existing_folder:
        raise HTTPException(status_code=400, detail="Folders already exist")
    
    new_folder = create_new_folder(db, folder_name, username, parent_name)

    return new_folder



"""
GET : Get all folders in Database
"""
@folderController.get("/{username}/")
async def get_user_folders(db: db_dependency, username: str):
    return db.query(Folders).filter(Folders.uploader == username).all()


"""
PUT : Update Folder name and update modified time
"""
@folderController.put("/{username}/update")
async def update_folder(
    db: db_dependency, username: str, existing_folder_name: str, new_folder_name: str
):
    existing_folder = get_folder_by_name(db, username, existing_folder_name)
    
    if not existing_folder:
        raise HTTPException(status_code=404, detail="Folder Not Found")

    update_folder_data(db, existing_folder, new_folder_name)

    return {
        "message": f"Folder name updated from {existing_folder_name} to {new_folder_name}"
    }

"""
DELETE : Delete Folder and all files & folders below
"""
@folderController.delete("/{username}/delete")
async def delete_folder(db: db_dependency, username: str, folder_name : str):
    if folder_name == "root":
        raise HTTPException(status_code=403, detail="Cannot Delete Root Folder")
    
    delete_folder_data(db, username, folder_name)
    
    return {
        "message": f"{folder_name} deleted"
    }

"""
PUT : Move Folder to another Folder
"""
@folderController.put("/{username}/move")
async def move_folder(db: db_dependency, username: str, folder_name : str, move_to_folder_name : str):
    move_folder_data(db, username, folder_name, move_to_folder_name)
    
    return {
        "message": f"{folder_name} moved to {move_to_folder_name}"
    }
    
"""
GET : Get Folder data
"""
@folderController.get("/{username}/{folder_name}")
async def get_folder(db: db_dependency, username: str, folder_name: str):
    folder_data = get_folder_data(db, username, folder_name)
    
    return folder_data
    