from argparse import ArgumentParser
from json import dump
from json import loads
from os import makedirs as mkdirs
from os import path
from sys import argv

import png

default_img = [[0 if ((x < 24) ^ (y // 8 is 1)) or x % 3 is 1 else 255 for x in range(48)] for y in range(16)]

gen_dirs = [
    'generated/assets/{0}/blockstates/',
    'generated/assets/{0}/models/block/',
    'generated/assets/{0}/models/item/',
    'generated/assets/{0}/textures/block/',
    'generated/assets/{0}/textures/item/',
    'generated/data/{0}/advancements/recipes/',
    'generated/data/{0}/loot_tables/blocks/',
    'generated/data/{0}/recipes/',
    'generated/data/{0}/tags/'
]

defaults = [
    '{"variants":{"":{"model":"{0}:block/{1}"}}}',
    '{"parent":"minecraft:block/cube_all","textures":{"all":"{0}:block/{1}"}}',
    '{"parent":"{0}:block/{1}"}',
    '{"type":"minecraft:crafting_shaped","pattern":["x"],"key":{"x":{"item":"minecraft:dirt"}},"result":{"item":"{0}:{1}"},"conditions":[]}',
    '{"parent":"minecraft:recipes/root","rewards":{"recipes":["{0}:{1}"]},"criteria":{},"requirements":[[]]}',
    '{"type":"minecraft:block","pools":[{"name":"{0}:{1}","rolls":1,"entries":[{"type":"minecraft:item","name":"{0}:{1}"}],"conditions":[{"condition":"minecraft:survives_explosion"}]}]}',
    '{"parent":"item/generated","textures":{"layer0":"{0}:item/{1}"}}'
]


def createDirs(mod_id):
    global gen_dirs
    [mkdirs(d.format(mod_id)) if not path.exists(d.format(mod_id)) else [] for d in gen_dirs]


def check(mod_id, name, data):
    if isinstance(data, dict):
        for s in data:
            data[s] = check(mod_id, name, data[s])
        return data
    elif isinstance(data, list):
        for i in range(len(data)):
            data[i] = check(mod_id, name, data[i])
        return data
    elif isinstance(data, str):
        return data.format(mod_id, name)
    else:
        return data


def load_default(mod_id, name, i):
    return check(mod_id, name, loads(defaults[i]))


def write(file, data):
    print("Creating {0}".format(file))
    with open(file, 'w+') as json_file:
        dump(data, json_file, indent=4)


def generate_texture(file):
    png.from_array(default_img, 'RGB').save(file)

def generateBlockFiles(mod_id, name):
    global gen_dirs
    json_file = '{0}.json'.format(name)
    png_file = '{0}.png'.format(name)
    write(gen_dirs[0].format(mod_id) + json_file, load_default(mod_id, name, 0))
    write(gen_dirs[1].format(mod_id) + json_file, load_default(mod_id, name, 1))
    write(gen_dirs[2].format(mod_id) + json_file, load_default(mod_id, name, 2))
    write(gen_dirs[7].format(mod_id) + json_file, load_default(mod_id, name, 3))
    write(gen_dirs[5].format(mod_id) + json_file, load_default(mod_id, name, 4))
    write(gen_dirs[6].format(mod_id) + json_file, load_default(mod_id, name, 5))
    generate_texture(gen_dirs[3].format(mod_id) + png_file)


def generateItemFiles(mod_id, name):
    global gen_dirs
    json_file = '{0}.json'.format(name)
    png_file = '{0}.png'.format(name)
    write(gen_dirs[7].format(mod_id) + json_file, load_default(mod_id, name, 3))
    write(gen_dirs[5].format(mod_id) + json_file, load_default(mod_id, name, 4))
    write(gen_dirs[2].format(mod_id) + json_file, load_default(mod_id, name, 6))
    generate_texture(gen_dirs[4].format(mod_id) + png_file)

def main(args_in=argv[1:]):
    parser = ArgumentParser()
    parser.add_argument('-b', '--block', nargs='+',
                        help='list of names of blocks to generate files for')
    parser.add_argument('-i', '--item', nargs='+',
                        help='list of names of items to generate files for')
    parser.add_argument('mod_id',
                        help='The id of your mod')
    args = parser.parse_args(args_in)

    createDirs(args.mod_id)
    for name in args.block:
        print("Processing the block {0}:{1}".format(args.mod_id, name))
        generateBlockFiles(args.mod_id, name)
    for name in args.item:
        print("Processing the item{0}:{1}".format(args.mod_id, name))
        generateItemFiles(args.mod_id, name)


if __name__ == '__main__':
    main()
