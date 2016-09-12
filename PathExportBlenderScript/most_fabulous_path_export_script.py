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

        filepath = self.filepath

        #TODO: move and split to separate methods (binary & java)
        for scene_obj in object_list:
            if scene_obj.type == 'CURVE' and scene_obj.name[:5] == 'kbap_':
                print("CURVE found for export: " + scene_obj.name)
                write_txt(scene_obj, filepath)

                #IMPORTANT NOTE: Blender Curve Paths MUST be in 'POLY' mode!
        
        return {'FINISHED'}
        
def menu_func(self, context):
    self.layout.operator(PathExporter.bl_idname, text="Export Animation Paths (.kbap");

######################## binary writer ####################

def write_binary(scene_obj):
    #TODO
    return

######################## java writer ######################

def write_txt(scene_obj, filepath):
    #TODO
    filepath_txt = filepath[:-5] + '_' + scene_obj.name + '.kbap'
    outputfile_txt = open(filepath_txt, 'w')

    if scene_obj.type != 'CURVE':
        print("euhm... shouldn't EVER get here!")

    for spline in scene_obj.data.splines:
        if spline.type != 'POLY':
            print("error: spline.type should be \'POLY\', found: \'" + spline.type + "\'")
        else:
            for point in spline.points:
                write_floats_text(point, outputfile_txt)
            print("success! file written: " + filepath_txt)

def write_floats_text(obj, file):
    print("%.5f, %.5f, %.5f, %.5f" % (obj.co[0], obj.co[1], obj.co[2], obj.co[3]))

    file.write("%.5f," % obj.co[0])
    file.write("%.5f," % obj.co[1])
    file.write("%.5f," % obj.co[2])
    file.write("%.5f," % obj.co[3])
    file.write("\n")

######################## add-in functions #################

def register():
    bpy.utils.register_class(PathExporter)
    bpy.types.INFO_MT_file_export.append(menu_func);
    
def unregister():
    bpy.utils.unregister_class(PathExporter)
    bpy.types.INFO_MT_file_export.remove(menu_func);

if __name__ == "__main__":
    register()