######################## add-in info ######################

bl_info = {
    "name": "Export Animation Paths",
    "category": "Import-Export",
    "author": "Julian Ewers-Peters",
    "location": "File > Import-Export"
}

######################## imports ##########################

import bpy
from bpy_extras.io_utils import ExportHelper
import os.path
import struct

######################## main #############################

class PathExporter(bpy.types.Operator, ExportHelper):
	"""Export Animation Paths"""
	bl_idname   = "export.export_paths"
    bl_label    = "Export Animation Paths"
    bl_options = {'REGISTER'}

    def execute(self, context):
	    ## get list of objects from scene ##
	    object_list = list(bpy.data.objects)
	    
	    return {'FINISHED'}
        
def menu_func(self, context):
    self.layout.operator(PathExporter.bl_idname, text="Export Animation Paths (.kbap");

######################## add-in functions #################

def register():
    bpy.utils.register_class(CollisionSphereExporter)
    bpy.types.INFO_MT_file_export.append(menu_func);
    
def unregister():
    bpy.utils.unregister_class(CollisionSphereExporter)
    bpy.types.INFO_MT_file_export.remove(menu_func);

if __name__ == "__main__":
    register()