import React, { useState, useEffect } from "react";
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from "./ui/dialog";
import { Button } from "./ui/button";
import { Input } from "./ui/input";
import { Label } from "./ui/label";
import { Badge } from "./ui/badge";
import { X, Plus } from "lucide-react";
import { Restaurant } from "./RestaurantCard";

interface AddRestaurantDialogProps {
  onAddRestaurant: (restaurant: Omit<Restaurant, "rating" | "favoriteCount">) => void;
  onEditRestaurant?: (restaurant: Omit<Restaurant, "rating" | "favoriteCount">) => void;
  restaurantToEdit?: Restaurant | null;
  open?: boolean;
  onOpenChange?: (open: boolean) => void;
}

export function AddRestaurantDialog({ 
  onAddRestaurant, 
  onEditRestaurant, 
  restaurantToEdit = null, 
  open, 
  onOpenChange 
}: AddRestaurantDialogProps) {
  
  // Always use controlled mode - provide defaults if not passed
  const isControlled = open !== undefined && onOpenChange !== undefined;
  const [internalOpen, setInternalOpen] = useState(false);
  
  const isOpen = isControlled ? open : internalOpen;
  const setIsOpen = isControlled ? onOpenChange : setInternalOpen;
  
  const isEditMode = restaurantToEdit !== null;

  const [formData, setFormData] = useState({
    id: "", // Add id field
    name: "",
    cuisine: "",
    description: "",
    tags: [] as string[],
    image: ""
  });
  const [currentTag, setCurrentTag] = useState("");

  // Reset form data when restaurant changes or dialog opens
  useEffect(() => {
    if (restaurantToEdit) {
      setFormData({
        id: restaurantToEdit.id, // Include id for edit mode
        name: restaurantToEdit.name,
        cuisine: restaurantToEdit.cuisine,
        description: restaurantToEdit.description,
        tags: restaurantToEdit.tags,
        image: restaurantToEdit.image || ""
      });
    } else {
      setFormData({
        id: "", // Empty id for add mode
        name: "",
        cuisine: "",
        description: "",
        tags: [],
        image: ""
      });
    }
  }, [restaurantToEdit, isOpen]);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (formData.name && formData.cuisine) {
      if (isEditMode && onEditRestaurant) {
        // For edit mode, include the id in the data
        onEditRestaurant(formData);
      } else {
        // For add mode, exclude the id from the data
        const { id, ...addData } = formData;
        onAddRestaurant(addData);
      }
      
      setFormData({
        id: "",
        name: "",
        cuisine: "",
        description: "",
        tags: [],
        image: ""
      });
      setIsOpen(false);
    }
  };

  const addTag = () => {
    if (currentTag && !formData.tags.includes(currentTag)) {
      setFormData(prev => ({ ...prev, tags: [...prev.tags, currentTag] }));
      setCurrentTag("");
    }
  };

  const removeTag = (tag: string) => {
    setFormData(prev => ({ ...prev, tags: prev.tags.filter(t => t !== tag) }));
  };

  // Render trigger button only when not controlled (uncontrolled mode)
  if (isControlled) {
    // Controlled mode - only render dialog content, no trigger
    return (
      <Dialog open={isOpen} onOpenChange={setIsOpen}>
        <DialogContent className="sm:max-w-md">
          <DialogHeader>
            <DialogTitle>{isEditMode ? "Edit Restaurant" : "Add New Restaurant"}</DialogTitle>
          </DialogHeader>
          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <Label htmlFor="restaurant-name">Restaurant Name *</Label>
              <Input
                  id="restaurant-name"
                  value={formData.name}
                  onChange={(e) => setFormData(prev => ({...prev, name: e.target.value}))}
                  required
              />
            </div>

            <div>
              <Label htmlFor="cuisine">Cuisine Type *</Label>
              <Input
                  id="cuisine"
                  value={formData.cuisine}
                  onChange={(e) => setFormData(prev => ({...prev, cuisine: e.target.value}))}
                  onKeyDown={(e) => {
                    if (e.key === 'Enter') {
                      e.preventDefault();
                    }
                  }}
                  required
              />
            </div>
            
            <div>
              <Label>Tags</Label>
              <div className="flex space-x-2 mb-2">
                <Input
                  placeholder="Add tag"
                  value={currentTag}
                  onChange={(e) => setCurrentTag(e.target.value)}
                  onKeyPress={(e) => e.key === 'Enter' && (e.preventDefault(), addTag())}
                />
                <Button type="button" onClick={addTag} size="sm">Add</Button>
              </div>
              <div className="flex flex-wrap gap-1">
                {formData.tags.map((tag) => (
                  <Badge key={tag} variant="secondary" className="gap-1">
                    {tag}
                    <X className="h-3 w-3 cursor-pointer" onClick={() => removeTag(tag)} />
                  </Badge>
                ))}
              </div>
            </div>
            
            <div>
              <Label htmlFor="image">Image URL</Label>
              <Input
                id="image"
                value={formData.image}
                onChange={(e) => setFormData(prev => ({ ...prev, image: e.target.value }))}
                placeholder="Optional - will use default if empty"
              />
            </div>
            
            <div className="flex justify-end space-x-2">
              <Button type="button" variant="outline" onClick={() => setIsOpen(false)}>
                Cancel
              </Button>
              <Button type="submit">{isEditMode ? "Save Changes" : "Add Restaurant"}</Button>
            </div>
          </form>
        </DialogContent>
      </Dialog>
    );
  }

  // Uncontrolled mode - render with trigger button
  return (
    <Dialog open={isOpen} onOpenChange={setIsOpen}>
      <DialogTrigger asChild>
        <button 
          data-slot="button"
          className="inline-flex items-center justify-center gap-2 whitespace-nowrap rounded-md text-sm font-medium transition-all disabled:pointer-events-none disabled:opacity-50 outline-none focus-visible:border-ring focus-visible:ring-ring/50 focus-visible:ring-[3px] aria-invalid:ring-destructive/20 dark:aria-invalid:ring-destructive/40 aria-invalid:border-destructive hover:bg-primary/90 h-9 px-4 py-2 bg-gradient-to-r from-orange-500 to-red-500 hover:from-orange-600 hover:to-red-600 text-white shadow-lg"
        >
          <Plus className="h-4 w-4 mr-2" />
          Add Restaurant
        </button>
      </DialogTrigger>
      <DialogContent className="sm:max-w-md">
        <DialogHeader>
          <DialogTitle>{isEditMode ? "Edit Restaurant" : "Add New Restaurant"}</DialogTitle>
        </DialogHeader>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <Label htmlFor="restaurant-name">Restaurant Name *</Label>
            <Input
                id="restaurant-name"
                value={formData.name}
                onChange={(e) => setFormData(prev => ({...prev, name: e.target.value}))}
                required
            />
          </div>

          <div>
            <Label htmlFor="cuisine">Cuisine Type *</Label>
            <Input
                id="cuisine"
                value={formData.cuisine}
                onChange={(e) => setFormData(prev => ({...prev, cuisine: e.target.value}))}
                onKeyDown={(e) => {
                  if (e.key === 'Enter') {
                    e.preventDefault();
                  }
                }}
                required
            />
          </div>
          
          <div>
            <Label>Tags</Label>
            <div className="flex space-x-2 mb-2">
              <Input
                placeholder="Add tag"
                value={currentTag}
                onChange={(e) => setCurrentTag(e.target.value)}
                onKeyPress={(e) => e.key === 'Enter' && (e.preventDefault(), addTag())}
              />
              <Button type="button" onClick={addTag} size="sm">Add</Button>
            </div>
            <div className="flex flex-wrap gap-1">
              {formData.tags.map((tag) => (
                <Badge key={tag} variant="secondary" className="gap-1">
                  {tag}
                  <X className="h-3 w-3 cursor-pointer" onClick={() => removeTag(tag)} />
                </Badge>
              ))}
            </div>
          </div>
          
          <div>
            <Label htmlFor="image">Image URL</Label>
            <Input
              id="image"
              value={formData.image}
              onChange={(e) => setFormData(prev => ({ ...prev, image: e.target.value }))}
              placeholder="Optional - will use default if empty"
            />
          </div>
          
          <div className="flex justify-end space-x-2">
            <Button type="button" variant="outline" onClick={() => setIsOpen(false)}>
              Cancel
            </Button>
            <Button type="submit">{isEditMode ? "Save Changes" : "Add Restaurant"}</Button>
          </div>
        </form>
      </DialogContent>
    </Dialog>
  );
}