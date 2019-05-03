#############
# ChestSort #
#############

You can define custom category files for ChestSort using simple .txt files.

If you have {category} in your sorting-method, it will get replaced with the category name.

Category names are determined by the file names. The name must start with a number ranging
from 000 to 899 and end with .txt

Default categories are prefixed with 900 to 999. Please do not edit the default categories.
You can instead copy or rename the default files and edit those instead.
WARNING: All files with names starting between 900 and 999 will be deleted on start.

If you put {keepCategoryOrder} behind {category} in the sorting-method, the items will be
ordered exactly as listed in the category files. Otherwise, they will be grouped by category
and then sorted according to the remaining variables in your sorting-method.

Category files can contain asterisks (*) as wildcard character at the beginning and/or end
of an expression, but not in the middle.

Category files can also contain comments using the hashtag (#) symbol