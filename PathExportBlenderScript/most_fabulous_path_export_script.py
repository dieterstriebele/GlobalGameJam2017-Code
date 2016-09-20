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

        for scene_obj in object_list:
            if scene_obj.type == 'CURVE' and scene_obj.name[:5] == 'kbap_':
                print("CURVE found for export: " + scene_obj.name)
                write_txt(scene_obj, filepath)
                write_binary(scene_obj, filepath)

                #IMPORTANT NOTE: Blender Curve Paths MUST be in 'POLY' mode!
        
        return {'FINISHED'}
        
def menu_func(self, context):
    self.layout.operator(PathExporter.bl_idname, text="Export Animation Paths (.kbap)");

######################## binary writer ####################

def write_binary(scene_obj, filepath):
    filepath_bin = filepath[:-5] + '_' + scene_obj.name + '.kbin'
    outputfile_bin = open(filepath_bin, 'wb')

    if scene_obj.type != 'CURVE':
        print("euhm... shouldn't EVER get here!")

    for spline in scene_obj.data.splines:
        if spline.type != 'POLY':
            print("error: spline.type should be \'POLY\', found: \'" + spline.type + "\'")
        else:
            pack_and_write_float((float(len(spline.points))), outputfile_bin)
            for point in spline.points:
                #transform to world coordinates and write to file
                v_co_w = scene_obj.matrix_world * point.co
                write_floats_binary(v_co_w, outputfile_bin)

    outputfile_bin.close()
    print("success! file written: " + filepath_bin)

def write_floats_binary(obj, file):
    pack_and_write_float((float(obj.x)), file)
    pack_and_write_float((float(obj.y)), file)
    pack_and_write_float((float(obj.z)), file)

def pack_and_write_float(float_data, file):
    # '>f' forces to write binary float in Big Endian format
    struct_out = struct.pack('>f', float_data)
    file.write(struct_out)

######################## txt writer ######################

def write_txt(scene_obj, filepath):
    filepath_txt = filepath[:-5] + '_' + scene_obj.name + '.kbap'    
    outputfile_txt = open(filepath_txt, 'w')

    if scene_obj.type != 'CURVE':
        print("euhm... shouldn't EVER get here!")

    for spline in scene_obj.data.splines:
        if spline.type != 'POLY':
            print("error: spline.type should be \'POLY\', found: \'" + spline.type + "\'")
        else:
            outputfile_txt.write("%.5f \n" % len(spline.points))
            for point in spline.points:
                #transform to world coordinates and write to file
                v_co_w = scene_obj.matrix_world * point.co
                print("%.5f, %.5f, %.5f" % (v_co_w.x, v_co_w.y, v_co_w.z))
                write_floats_text(v_co_w, outputfile_txt)

    outputfile_txt.close()
    print("success! file written: " + filepath_txt)

def write_floats_text(obj, file):
    file.write("%.5f " % obj.x)
    file.write("%.5f " % obj.y)
    file.write("%.5f " % obj.z)
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