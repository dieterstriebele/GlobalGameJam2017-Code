######################## add-in info ######################

bl_info = {
    "name": "Export Animation Paths",
    "category": "Import-Export",
    "author": "Julian Ewers-Peters",
    "location": "File > Import-Export"
}

######################## imports ##########################

import bpy
import bpy.types
from bpy_extras.io_utils import ExportHelper
import os.path
import struct

######################## main #############################

class PathExporter(bpy.types.Operator, ExportHelper):
    """Export Animation Paths"""
    bl_idname   = "export.export_paths"
    bl_label    = "Export Animation Paths"
    bl_options  = {'REGISTER'}

    filename_ext = ".kbap"

    def execute(self, context):
        ## get list of objects from scene ##
        object_list = list(bpy.data.objects)

        #TODO: move and split to separate methods (binary & java)
        for scene_obj in object_list:
            if scene_obj.type == 'CURVE' and scene_obj.name[:5] == 'kbap_':
                print("CURVE found for export: " + scene_obj.name)
        
        return {'FINISHED'}
        
def menu_func(self, context):
    self.layout.operator(PathExporter.bl_idname, text="Export Animation Paths (.kbap");

######################## binary writer ####################

def write_binary(object_list):
    #TODO
    return

######################## java writer ######################

def write_java(object_list):
    #TODO
        #iterate over objects
            #iterate over curves (obj.data.curves)
                #iterate over splines
                    #interpolate/calculate discrete points
                    #write out points
    return

######################## add-in functions #################

def register():
    bpy.utils.register_class(PathExporter)
    bpy.types.INFO_MT_file_export.append(menu_func);
    
def unregister():
    bpy.utils.unregister_class(PathExporter)
    bpy.types.INFO_MT_file_export.remove(menu_func);

if __name__ == "__main__":
    register()